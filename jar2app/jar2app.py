#!/usr/bin/env python
# -*- coding: utf-8 -*-
##
## Copyright (C) 2015-2017 João Ricardo Lourenço <jorl17.8@gmail.com>
##
## Github: https://github.com/Jorl17
##
## Project main repository: https://github.com/Jorl17/jar2app
##
## This file is part of jar2app.
##
## jar2app is free software: you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation, either version 2 of the License, or
## (at your option) any later version.
##
## jar2app is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
##
## You should have received a copy of the GNU General Public License
## along with jar2app.  If not, see <http://www.gnu.org/licenses/>.
##
from optparse import OptionParser
import os.path
import shutil
import tempfile
from zipfile import ZipFile
import sys

__author__ = 'jorl17'
VERSION = '1.0.1'

# Python 2 compatibility
is_python2 = sys.version_info[0] == 2
if is_python2:
    FileExistsError = OSError



#------------------------------------------------------------------------------
# Defaults
#------------------------------------------------------------------------------
DEFAULT_VERSION='1.0.0'
DEFAULT_BUNDLE_IDENTIFIER_PREFIX='com.jar2app.example.'
DEFAULT_SIGNATURE='????'

#------------------------------------------------------------------------------
# The info.plist file with placeholders
#------------------------------------------------------------------------------
info_plist = """<?xml version="1.0" ?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
    <key>CFBundleDevelopmentRegion</key>
    <string>English</string>

    <key>CFBundleExecutable</key>
    <string>JavaAppLauncher</string>

    <key>CFBundleIconFile</key>
    <string>{icon}</string>

    <key>CFBundleIdentifier</key>
    <string>{bundle_identifier}</string>

    <key>CFBundleDisplayName</key>
    <string>{bundle_displayname}</string>

    <key>CFBundleInfoDictionaryVersion</key>
    <string>6.0</string>

    <key>CFBundleName</key>
    <string>{bundle_name}</string>

    <key>CFBundlePackageType</key>
    <string>APPL</string>

    {retina_support}

    <key>CFBundleShortVersionString</key>
    <string>{short_version_string}</string>

    <key>CFBundleSignature</key>
    <string>{unique_signature}</string>

    <key>CFBundleVersion</key>
    <string>{bundle_version}</string>

    <key>NSHumanReadableCopyright</key>
    <string>{copyright}</string>

    {jdk}

    <key>JVMMainClassName</key>
    <string>{main_class_name}</string>

    <key>JVMOptions</key>
    <array>
{jvm_options}
    </array>

    <key>JVMArguments</key>
    <array>
{jvm_arguments}
    </array>

    </dict>
</plist>
"""

retina_support_string = """<key>NSPrincipalClass</key>
    <string>NSApplication</string>
    <key>NSHighResolutionCapable</key>
    <string>True</string>"""


#------------------------------------------------------------------------------
# Create a directory and ignore the "File already exists" error
#------------------------------------------------------------------------------
def mkdir_ignore_exists(p):
    try:
        os.mkdir(p)
        return True
    except FileExistsError:
        return False

#------------------------------------------------------------------------------
# Make a file executable
#------------------------------------------------------------------------------
def make_executable(path):
    mode = os.stat(path).st_mode
    mode |= (mode & 0o444) >> 2
    os.chmod(path, mode)

#------------------------------------------------------------------------------
# Just strip the extension from a name
#------------------------------------------------------------------------------
def strip_extension_from_name(name):
    return os.path.splitext(name)[0]

#------------------------------------------------------------------------------
# Determine the main class in a JAR file. This basically involves searching
# through the JAR (it's just a zip file), locating the MANIFEST.MF file,
# decompressing it and then finding the main-class line.
#------------------------------------------------------------------------------
def find_jar_mainclass(jar_file):
    f = ZipFile(jar_file, 'r')
    for file in f.infolist():
        orig_fn = file.filename
        lower_fn = orig_fn.lower()
        if lower_fn.startswith('meta-inf') and lower_fn.endswith('manifest.mf'):
            manifest_mf = f.read(orig_fn)
            for line in manifest_mf.decode().split('\n'):
                if line.strip().lower().startswith('main-class'):
                    return line.split(':')[1].strip()

#------------------------------------------------------------------------------
# Build the main directory structure of the .App. It should look like
# <appname>.App/
#              Contents/
#                       Java/
#                       MacOS/
#                       Resources/
#                                 en.lproj/
#------------------------------------------------------------------------------
def build_directory_structure(app_full_path):
    mkdir_ignore_exists(os.path.dirname(app_full_path)) #Base output directory where the app is placed. Create it.
    mkdir_ignore_exists(app_full_path)
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents'))
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents', 'Java'))
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents', 'MacOS'))
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents', 'PlugIns'))
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents', 'Resources'))
    mkdir_ignore_exists(os.path.join(app_full_path, 'Contents', 'Resources', 'en.lproj'))

#------------------------------------------------------------------------------
# Write the plist file in the desired output folder. Note that these arguments
# are passed directly to the info_plist string, so some of them (jdk,
# jvm_arguments...) should have a bit of XML.
#
# The destination folder is typically <appname>.App/Contents
#------------------------------------------------------------------------------
def create_plist_file(destination_folder, icon, bundle_identifier, bundle_displayname, bundle_name,bundle_version,
                      short_version_string,copyright_str, main_class_name, jvm_arguments, jvm_options, jdk,
                      unique_signature, retina_support):
    filled_info_plist=info_plist.format(icon=icon,
                                        bundle_identifier=bundle_identifier,
                                        bundle_displayname=bundle_displayname,
                                        bundle_name=bundle_name,
                                        bundle_version=bundle_version,
                                        short_version_string=short_version_string,
                                        copyright=copyright_str,
                                        main_class_name=main_class_name,
                                        jvm_arguments=jvm_arguments,
                                        jvm_options=jvm_options,
                                        jdk=jdk,
                                        unique_signature=unique_signature,
                                        retina_support=retina_support)

    with open(os.path.join(destination_folder, 'Info.plist'), 'w') as f:
        f.write(filled_info_plist)

#------------------------------------------------------------------------------
# Convert a sequence of strings, separated by spaces, into a sequence of
# <string></string> strings.
# E.g., "a b c" becomes "<string>a</string>b<string></string><string>c</string>
#
# This is to be used for the JVMArguments and JVMOptions in the plist.xml file.
# Also note that there is some whitespace added. This is to comply with the
# indent of the xml file.
#------------------------------------------------------------------------------
def string_to_plist_xmlarray_values(s):
    if not s:
        return ''
    return  '        <string>' + '</string>\n        <string>'.join( [i.strip() for i in s.split() ] ) + '</string>'

#------------------------------------------------------------------------------
# Check if JDK/JRE is valid. It can be a zip file or it can be a directory.
# Returns:
#   * The xml string to use
#   * The JDK/JRE folder name (not its full path; e.g. for zip files, it strips
#     the zip extension)
#   * Whether the JDK/JRE is a file or a directory
#------------------------------------------------------------------------------
def determine_jdk(jdk):
    if not jdk:
        return '','',True
    isfile = os.path.isfile(jdk)
    if isfile:
        if not jdk.lower().endswith('.zip'):
            exit('JDK/JRE file is not a zip file.')
        jdk = strip_extension_from_name(os.path.basename(jdk))

    dir, name = os.path.split(jdk)
    return '<key>JVMRuntime</key>\n<string>' + name + '</string>',jdk,isfile

#------------------------------------------------------------------------------
# Copy a JDK to the bundled .app. The app_full_path should be the root of
# the app (e.g. Test.app/). JDK should be the path to the JDK/JRE and
# jdk_isfile comes from determine_jdk and indicates if the this JDK is a zip
# file or a directory.
#
# In case it's a directory, we just copy it over. If it's a zip file, we must
# first decompress it.
#
# In general, the JVM should go to <appname>.App/Contents/PlugIns, e.g. the
# structure might become
# <appname>.App/Contents/PlugIns/jdk1.8.0_40.jdk
#
# This JDK should be in the format expected by AppBundler (check if the first
# directory is just a Contents folder)
#------------------------------------------------------------------------------
def copy_jdk(app_full_path, jdk, jdk_isfile):
    if jdk:
        if jdk_isfile:
            tmpdir = tempfile.mkdtemp()
            f = ZipFile(jdk, 'r')
            f.extractall(tmpdir)
            jdk_dir = tmpdir
            try:
                destination_path = os.path.join(app_full_path, 'Contents', 'PlugIns')
                os.rmdir(destination_path)
                shutil.copytree(jdk_dir, destination_path)
            except FileExistsError as e:
                raise # FIXME
            try:
                base_path = os.path.join(app_full_path, 'Contents', 'PlugIns')
                dir = os.listdir(base_path)[0]
                final_dir = os.path.join(base_path, strip_extension_from_name(os.path.basename(jdk)))
                shutil.rmtree(final_dir, ignore_errors=True)  # Delete old folder (if it exists)
                os.rename(os.path.join(base_path, dir), final_dir)
                shutil.rmtree(tmpdir)
            except:
                raise #FIXME
        else:
            destination = os.path.join(app_full_path, 'Contents', 'PlugIns', os.path.basename(jdk))
            shutil.rmtree(destination, ignore_errors=True) # Delete old folder (if it exists)
            shutil.copytree(jdk, destination, symlinks=True)


# ------------------------------------------------------------------------------
# Copy all files while also preserving status information. If status cannot be
# copied, drop it and copy mode instead.
# ------------------------------------------------------------------------------
def copy_preserve_status(src, dst):
    try:
        shutil.copy2(src, dst)
    except OSError:
        shutil.copy(src, dst)

#------------------------------------------------------------------------------
# Copy all files to the previously created directory. This involes copying
# the Localizable.strings file, the JavaAppLauncher executable and, finally,
# the JDK/JRE and application icon if they were provided
#------------------------------------------------------------------------------
def copy_base_files(app_full_path, icon, jar_file, jdk, jdk_isfile):
    if icon:
        copy_preserve_status(icon,os.path.join(app_full_path, 'Contents', 'Resources'))
    copy_preserve_status(os.path.join(os.path.dirname(sys.argv[0]), 'jar2app_basefiles', 'Localizable.strings'), os.path.join(app_full_path, 'Contents', 'Resources', 'en.lproj', 'Localizable.strings'))
    copy_preserve_status(os.path.join(os.path.dirname(sys.argv[0]), 'jar2app_basefiles', 'JavaAppLauncher'), os.path.join(app_full_path, 'Contents', 'MacOS', 'JavaAppLauncher'))
    make_executable(os.path.join(app_full_path, 'Contents', 'MacOS', 'JavaAppLauncher'))
    copy_preserve_status(jar_file, os.path.join(app_full_path, 'Contents', 'Java', os.path.basename(jar_file)))
    copy_jdk(app_full_path, jdk, jdk_isfile)

#------------------------------------------------------------------------------
# Determine the destination Appname (and full path) taking into account the
# parameters. Note that:
# 1. If output is provided and it is a destination file, its name is used as
#    the appname
# 2. If output is provided but it is a destination folder, then name must be
#    figured out with the next steps (as if output wasn't provided).
# 3. Prefer the bundle name
# 4. Prefer the bundle displayname
# 5. Prefer the jar name (without extension, of course)
# We also assume that by default the output should go to the current directory.
#------------------------------------------------------------------------------
def determine_app_name(jar_name, output, bundle_displayname, bundle_name, auto_append_app):
    if output:
        dir,name = os.path.split(output)
        if not dir: #All that was given was a filename
            dir = '.'
    else: #Assume default directory
        dir = '.'
        name = ''

    if not name:
        # If no .app name is provided, prefer:
        # 1. The bundle name, if it was provided
        # 2. the bundle_displayname, if it was provided
        # 3. The jar name
        if bundle_name:
            return os.path.join(dir,bundle_name + '.app')
        elif bundle_displayname:
            return os.path.join(dir,bundle_displayname + '.app')
        elif jar_name:
            return os.path.join(dir,strip_extension_from_name(jar_name) + '.app')
    else:
        # Ensure the name ends with .app, unless we were told not to do so
        if auto_append_app:
            if name.lower().endswith('.app'):
                return os.path.join(dir,name)
            else:
                return os.path.join(dir,name + '.app')
        else:
            return os.path.join(dir,name)

#------------------------------------------------------------------------------
# Print summary info on the fields used, if they are used. Used when the
# process is done
#------------------------------------------------------------------------------
def print_final_file_info(icon, bundle_identifier, bundle_displayname, bundle_name, short_version_string,
                          unique_signature, bundle_version, copyright_str, orig_jvm_options, main_class_name,
                          jdk, retina_support, use_screen_menu_bar, working_directory):
    def print_field_if_not_null(name, field):
        if field:
            print('{}: {}'.format(name, field))

    print_field_if_not_null('CFBundleIconFile', icon)
    print_field_if_not_null('CFBundleIdentifier', bundle_identifier)
    print_field_if_not_null('CFBundleDisplayName', bundle_displayname)
    print_field_if_not_null('CFBundleName', bundle_name)
    print_field_if_not_null('CFBundleShortVersionString', short_version_string)
    print_field_if_not_null('CFBundleSignature', unique_signature)
    print_field_if_not_null('CFBundleVersion', bundle_version)
    print_field_if_not_null('NSHumanReadableCopyright', copyright_str)
    if retina_support:
        print('Retina support enabled.')

    if use_screen_menu_bar:
        print('macOS menubar support enabled (might not always work).')

    print('---')
    print_field_if_not_null('JVMOptions', orig_jvm_options)
    print_field_if_not_null('JVMMainClassName', main_class_name)
    print_field_if_not_null('JVMRuntime', jdk)
    print_field_if_not_null('JAR Working directory', working_directory)

#------------------------------------------------------------------------------
# This is the main application logic. It receives the arguments straight from
# the user, gives appropriate defaults, builds the directory structure,
# copies files (packing the JDK/JRE) and creates the plist file. In the end,
# if all went well, it displays summary info.
#------------------------------------------------------------------------------
def make_app(jar_file, output='.', icon=None, bundle_identifier=None, bundle_displayname=None, bundle_name=None,
             bundle_version=None, short_version_string=None, copyright_str=None, main_class_name=None,
             jvm_arguments=None, jvm_options=None, jdk=None, unique_signature=None, auto_append_app=True,
             retina_screen=True, use_screen_menu_bar=False, working_directory=None):
    def default_value(d, default):
        return d if d else default

    orig_jvm_options  = jvm_options
    if not jvm_options: jvm_options = ''
    jar_name          = os.path.basename(jar_file)
    app_full_path     = determine_app_name(jar_name, output, bundle_displayname, bundle_name, auto_append_app)
    app_name          = strip_extension_from_name(os.path.basename(app_full_path))
    icon              = default_value(icon, '')
    bundle_identifier = default_value(bundle_identifier, DEFAULT_BUNDLE_IDENTIFIER_PREFIX + app_name)

    if jdk:
        # Remove any trailing forward and backslashes which might screw up os.path.basename when copying the JDK.
        jdk = jdk.rstrip('/').rstrip('\\')

    if use_screen_menu_bar:
        jvm_options += ' -Dapple.laf.useScreenMenuBar=true'

    if working_directory:
        jvm_options += ' -Duser.dir=%s' % working_directory

    if not bundle_displayname:
        # If no bundle_displayname is provided:
        # 1. Use the bundle_name
        # 2. use the app_name (note that the app_name was already determined based on what the user gave us.
        #    For instance, if no app_name was given, and no displayname was given, and no app name was given, the
        #    first choice is the bundle_name.
        if bundle_name:
            bundle_displayname = bundle_name
        else:
            bundle_displayname = app_name

    # When we get here, we always have a displayname. So if there's no bundlename, go with that. It may itself have
    # come from the app name
    bundle_name = default_value(bundle_name, bundle_displayname)

    if not bundle_version:
            bundle_version = short_version_string if short_version_string else DEFAULT_VERSION

    # When we get here, we always have bundle_version, even if it is the default
    short_version_string        = default_value(short_version_string, bundle_version)
    copyright_str               = default_value(copyright_str, '')
    main_class_name             = default_value(main_class_name, find_jar_mainclass(jar_file))
    unique_signature            = default_value(unique_signature, '????')
    jvm_arguments               = string_to_plist_xmlarray_values(jvm_arguments)
    jvm_options                 = string_to_plist_xmlarray_values(jvm_options)
    jdk_xml,jdk_name,jdk_isfile = determine_jdk(jdk)

    if retina_screen:
        retina_screen = retina_support_string
    else:
        retina_screen = ''

    print('Packing {} into {}'.format(jar_file, os.path.abspath(app_full_path)))

    build_directory_structure(app_full_path)
    create_plist_file(os.path.join(app_full_path, 'Contents'), os.path.basename(icon), bundle_identifier,
                      bundle_displayname, bundle_name,bundle_version,short_version_string,copyright_str,
                      main_class_name, jvm_arguments, jvm_options, jdk_xml, unique_signature, retina_screen)
    copy_base_files(app_full_path, icon, jar_file, jdk, jdk_isfile)

    print_final_file_info(icon, bundle_identifier, bundle_displayname, bundle_name, short_version_string,
                          unique_signature, bundle_version, copyright_str, orig_jvm_options, main_class_name,
                          jdk_name, retina_screen, use_screen_menu_bar, working_directory)

    print("\n{} packaged to {}.".format(jar_file, os.path.abspath(app_full_path)))

def parse_input():
    parser = OptionParser()
    parser.add_option('-n', '--name', help='Package/Bundle name.', dest='bundle_name', type='string', default=None)
    parser.add_option('-d', '--display-name', help='Package/Bundle display name.', dest='bundle_displayname', type='string',default=None)
    parser.add_option('-i', '--icon',help='Icon (in .icns format). (Default: None)', dest='icon', type='string', default=None)
    parser.add_option('-b', '--bundle-identifier', help='Package/Bundle identifier (e.g. com.example.test) (Default is application name prefix by {}.'.format(DEFAULT_BUNDLE_IDENTIFIER_PREFIX), dest='bundle_identifier',type='string', default=None)
    parser.add_option('-v', '--version', help='Package/Bundle version (e.g. 1.0.0) (Default: {}).'.format(DEFAULT_VERSION),dest='bundle_version', type='string', default=DEFAULT_VERSION)
    parser.add_option('-s', '--short-version', help='Package/Bundle short version (see Apple\'s documentation on CFBundleShortVersionString) (Default: {}).'.format(DEFAULT_VERSION), dest='short_version_string',type='string', default=DEFAULT_VERSION)
    parser.add_option('-c', '--copyright',help='Package/Bundle copyright string (e.g. (c) 2015 Awesome Person) (Default: empty)',dest='copyright_str', type='string', default=None)
    parser.add_option('-u', '--unique-signature', help='4 Byte unique signature of your application (Default: {})'.format(DEFAULT_SIGNATURE),dest='signature', type='string', default=DEFAULT_SIGNATURE)
    parser.add_option('-m', '--main-class', help='Jar main class. Blank for auto-detection (usually right).',dest='main_class_name', type='string', default=None)
    parser.add_option('-r', '--runtime', help='JRE/JDK runtime to bundle. Can be a folder or a zip file. If none is given, the default on the system is used (default: None)',dest='jdk', type='string', default=None)
    parser.add_option('-j', '--jvm-options',help='Extra JVM options. Place one by one, separated by spaces, inside single quotes (e.g. -o \'-Xmx1024M -Xms256M\'). (Default: None)',dest='jvm_options', type='string', default=None)
    parser.add_option('-a', '--no-append-app-to-name', help='Do not try to append .app to the output file by default.', dest='auto_append_name', action='store_false')
    parser.add_option('-l', '--low-res-mode', help='Do not try to report retina-screen capabilities (use low resolution mode; by default high resolution mode is used).',dest='retina_screen', action='store_false')
    parser.add_option('-o', '--use-osx-menubar', help='Use OSX menu bar instead of Java menu bar (Default: False).', dest='use_screen_menu_bar', action='store_true')
    parser.add_option('-w','--working-directory', help='Set current working directory (user.dir) on launch (Default: $APP_ROOT/Contents).', dest='working_directory', type='string', default='$APP_ROOT/Contents')

    (options, args) = parser.parse_args()

    if len(args) == 2:
        input_file = args[0]
        output = args[1]
    elif len(args) > 2:
        parser.error('Extra arguments provided!')
    elif len(args) == 1:
        input_file = args[0]
        output = None
    else:
        parser.error('An input file is needed. Optionally, you can also provide an output file or directory. E.g.\n{} in.jar\n{} in.jar out.app\n{} in.jar out/'.format(sys.argv[0], sys.argv[0], sys.argv[0]) )

    if options.auto_append_name == None:
        options.auto_append_name = True

    if options.retina_screen == None:
        options.retina_screen = True

    jvm_arguments = ''

    return input_file, output, options.icon, options.bundle_identifier, options.bundle_displayname, options.bundle_name,\
           options.bundle_version, options.short_version_string, options.copyright_str, options.main_class_name,\
           jvm_arguments, options.jvm_options, options.jdk, options.signature, options.auto_append_name,\
           options.retina_screen, options.use_screen_menu_bar, options.working_directory

def main():
    print('jar2app %s, João Ricardo Lourenço, 2015-2017 <jorl17.8@gmail.com>.' % VERSION)
    print('Github page: https://github.com/Jorl17/jar2app/')
    make_app(*parse_input())

main()