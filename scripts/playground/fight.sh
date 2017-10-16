# !/bin/bash





javac FruitSolver.java

startTotal=$(date +%s.%N)

start=$(date +%s.%N)
end=$(date +%s.%N)
dif=$(echo "$end" - "$start" | bc)

# n=$(shuf -i 1-26 -n 1)
# f=$(shuf -i 1-9 -n 1)
# python generator.py $n $f
# sleep 0.1
# echo "$n $f"

head -n3 data/input.txt
head -n3 data/input.txt > data/temp.txt
threhsold=300
count=0
while [ $(bc <<< "$dif <= $threhsold") -eq 1 ]
do
	localStart=$(date +%s.%N)
	java FruitSolver
	cat data/temp.txt > data/input.txt
	tail -n+2 data/output.txt >> data/input.txt

	if ! tail -n+4 data/input.txt | grep -q "[0-9]"
		then
		echo "done"
		break
	fi
	end=$(date +%s.%N)
	dif=$(echo "$end" - "$start" | bc)
	localDif=$(echo "$end" - "$localStart" | bc)
	# echo "iteration time: $localDif"
 	count=`expr $count + 1`
done

endTotal=$(date +%s.%N)
difTotal=$(echo "$endTotal" - "$startTotal" | bc)
echo "total time: $difTotal"
echo "total steps: $count"
