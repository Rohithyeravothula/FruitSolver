# !/bin/bash





javac FruitSolver.java

startTotal=$(date +%s.%N)

start=$(date +%s.%N)
end=$(date +%s.%N)
dif=$(echo "$end" - "$start" | bc)
threhsold=1000

count=0
while [ $count -le 2 ]
do
	count=`expr $count + 1`
	n=$(shuf -i 1-26 -n 1)
	f=$(shuf -i 1-9 -n 1)
	python generator.py $n $f
	sleep 0.1
	# echo "$n $f"

	head -n3 data/input.txt
	head -n3 data/input.txt > data/temp.txt

	while [ $(bc <<< "$dif <= $threhsold") -eq 1 ]
	do
		java FruitSolver
		end=$(date +%s.%N)
		dif=$(echo "$end" - "$start" | bc)
		cat data/temp.txt > data/input.txt
		tail -n+2 data/output.txt >> data/input.txt

		if ! tail -n+4 data/input.txt | grep -q "[0-9]"
			then
			echo "done"
			break
		fi
		
	done

	endTotal=$(date +%s.%N)
	difTotal=$(echo "$endTotal" - "$startTotal" | bc)
	echo "board: $n fruits: $f time: $difTotal" >> timeinfo.txt
done
