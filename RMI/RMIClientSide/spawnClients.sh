#! /bin/bash

limit=$1
for (( i = 1; i <= limit; i++))
do
	
	java -classpath /Users/A_Y_M_A_N/Documents/workspace/RMIClientSide/bin ClientDriver $i

   #echo "Welcome $i times"
done
