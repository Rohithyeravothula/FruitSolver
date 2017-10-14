import sys
import math
import random


def generate_test(size, fruits, fileName):
	a=[]
	for i in range(0, size):
		b = [-1] * size
		a.append(b)

	count = 0
	maxC = size*size
	while count < maxC:
		i = random.randint(0, size-1)
		j = random.randint(0, size-1)
		f = random.randint(0, int(math.sqrt(fruits))-1)
		if a[i][j] == -1:
			a[i][j] = f

		count += 1

	for i in range(0, size):
		for j in range(0, size):
			if a[i][j] == -1:
				a[i][j] = random.randint(0, fruits)

	# print(len(a[0]), len(a))
	write_to_file(fileName, size, fruits, 12, a)

def write_to_file(fileName, size, fruits_count, time, a):
	ary = str(size) + "\n" + str(fruits_count) + "\n" + str(time) + "\n"
	for i in range(0, size):
		for j in range(0, size):
			ary += str(a[i][j])
		ary += "\n"
	ary = ary[:-1]
	f = open(fileName, 'w')
	f.write(ary)
	f.close()

if __name__ == '__main__':
	nInp=int(sys.argv[1])
	fInp=int(sys.argv[2])
	generate_test(nInp, fInp, "data/input.txt")
