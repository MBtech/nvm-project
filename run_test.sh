#!/bin/bash


for j in `seq 0.7 0.1 0.9`
do
for i in `seq 1 5`
do
celery worker -A tasks -l info -Q node0 & 
pid=$!
echo $pid

python master.py $j
./get_avg.sh >> result_$j

jps | grep "RMI_Client.jar" | awk '{print $1}' | xargs -I {} kill -9 {}

rm snapshot_*
kill -9 $pid
pkill celery

done
done
