#!/bin/bash

# javac FruitSolver.java
# head -n3 data/input.txt > data/temp.txt

# startTotal=$(date +%s.%N)

# start=$(date +%s.%N)
# end=$(date +%s.%N)
# dif=$(echo "$end" - "$start" | bc)
# threhsold=1000

count=0
while [ $count -le 300 ]
do
	count=`expr $count + 1`
	n=$(shuf -i 1-26 -n 1)
	f=$(shuf -i 1-9 -n 1)
	python generator.py $n $f

	while [ $(bc <<< "$dif <= $threhsold") -eq 1 ]
	do
		java FruitSolver
		end=$(date +%s.%N)
		dif=$(echo "$end" - "$start" | bc)
		cat data/temp.txt > data/input.txt
		tail -n+2 data/output.txt >> data/input.txt

		tail -n+4 data/input.txt > data/compare.txt
		inp="data/compare.txt"
		stp="data/stop.txt"
		if cmp -s "$inp" "$stp"
			then
			break
		fi
	done

	endTotal=$(date +%s.%N)
	difTotal=$(echo "$endTotal" - "$startTotal" | bc)
	echo $difTotal
done
