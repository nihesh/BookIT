# Author: Nihesh Anderson K
# Date  : Nov 13, 2017

import os

if(__name__ == "__main__"):
	Ham = os.listdir("./Ham")
	Ham.sort()
	counter=1
	for val in Ham:
		os.rename("./Ham/"+val, "./Ham/"+str(counter)+".txt")
		counter+=1

	Spam = os.listdir("./Spam")
	Spam.sort()
	counter=1
	for val in Spam:
		os.rename("./Spam/"+val, "./Spam/"+str(counter)+".txt")
		counter+=1