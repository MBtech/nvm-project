#!/bin/bash

sum=0
for i in `seq 0 8`
do
   float=$(tail -1 snapshot_$i | awk -F, '{print $5}')
   int=${float%.*}
#   echo $int
   sum=$(($int + $sum))   

done

total=9
echo $(( sum / total))
