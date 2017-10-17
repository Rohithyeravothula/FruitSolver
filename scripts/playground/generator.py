import time, sys, os
import math
import random

def gravity(a):
	n=len(a)
	for j in range(0,n):
		b = []
		for i in range(0,n):
			if a[i][j] != -1:
				b.append(a[i][j])
		# print(b)
		b = [-1]*(n-len(b)) + b
		for i in range(0, n):
			a[i][j] = b[i]


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
		f = random.randint(-1, int(math.sqrt(fruits)))
		if a[i][j] == -1:
			a[i][j] = f

		count += 1

	for i in range(0, size):
		for j in range(0, size):
			if a[i][j] == -1:
				a[i][j] = random.randint(0, fruits)

	# print(len(a[0]), len(a))
	gravity(a)
	time = (random.random() + 0.5) * (size**2)
	# print(a)
	write_to_file(fileName, size, fruits, time, a)

def write_to_file(fileName, size, fruits_count, time, a):
	ary = str(size) + "\n" + str(fruits_count) + "\n" + str(time) + "\n"
	for i in range(0, size):
		for j in range(0, size):
			if(a[i][j] == -1):
				ary += "*"
			else:
				ary += str(a[i][j])
		ary += "\n"
	ary = ary[:-1]
	# print(ary)
	f = open(fileName, 'w')
	f.write(ary)
	f.close()


def randomTest():
	for i in range(0, 100):
		generate_test(random.randint(1, 26), random.randint(0, 9), "data/input.txt")
		time.sleep(1)
		os.system('clear')


def defined_test():
	n = [10, 15, 20, 26]
	f = [2, 6, 9]
	for i in n:
		for j in f:
			generate_test(i, j, "data/input" + str(i) + "-" + str(j) + " .txt")



if __name__ == '__main__':
	nInp=int(sys.argv[1])
	fInp=int(sys.argv[2])
	generate_test(nInp, fInp, "data/input.txt")
	# defined_test()
