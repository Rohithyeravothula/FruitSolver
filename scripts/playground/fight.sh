#!/bin/bash

javac FruitSolver.java
head -n3 data/input.txt > data/temp.txt

startTotal=$(date +%s.%N)

start=$(date +%s.%N)
end=$(date +%s.%N)
dif=$(echo "$end" - "$start" | bc)
threhsold=1000
# echo $(bc <<< "$dif <= $threhsold") -eq 1
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

