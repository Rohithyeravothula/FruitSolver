import math
import random


def generate_test(n, fruits, fileName):
	a=[]
	for i in range(0, n):
		b = [-1] * n
		a.append(b)

	count = 0
	maxC = n*n
	while count < maxC:
		i = random.randint(0, n-1)
		j = random.randint(0, n-1)
		f = random.randint(0, int(math.sqrt(fruits))-1)
		if a[i][j] == -1:
			a[i][j] = f

		count += 1

	for i in range(0, n):
		for j in range(0, n):
			if a[i][j] == -1:
				a[i][j] = random.randint(0, fruits)

	print(len(a[0]), len(a))
	write_to_file(fileName, n, fruits, 12, a)

def write_to_file(fileName, n, fruits, time, a):
	ary = str(n) + "\n" + str(fruits) + "\n" + str(time) + "\n"
	for i in range(0, n):
		for j in range(0, n):
			ary += str(a[i][j])
		ary += "\n"
	ary = ary[:-1]
	f = open(fileName, 'w')
	f.write(ary)
	f.close()


def tests():
	maxN = 26
	maxF = 10
	n = random.randint(2, maxN)
	f = random.randint(1, maxF)
	t = 0
	while t<10:
		generate_test(n, f)

import sys
if __name__ == '__main__':
	n=sys.argv[1]
	f=sys.argv[2]
	generate_test(n, f, "tests/output.txt")
