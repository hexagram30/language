import random
from string import strip

def randomLine(filename):
	"Retrieve a  random line from a file, reading through the file once"
	fh = open(filename, "r")
	lineNum = 0
	it = ''

	while 1:
		aLine = fh.readline()
		lineNum = lineNum + 1
		if aLine != "":
			#
			# How likely is it that this is the last line of the file ? 
			if random.uniform(0,lineNum)<1:
				it = aLine
		else:
			break

	fh.close()

	return it

for i in range(1028):
  line = randomLine("arabic.txt")
  line = strip(line) 
  print line

