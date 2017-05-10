import csv
import numpy as np
import random
import bisect 
import time

### This python file is to generate failures.csv file that has a trace of failures
### that we will use for our purposes

prf = 0.1 #Probability of multiple machine failing at the same time
rack = 3 # No. of machine for simultaneous failure scenario
N = 9 # Total Number of machines/processes
mttf = 25 # Time between failures (in sec)
T = 300 #1hr (trace duration)

failure_file = open('failures.csv', 'wb')
writer = csv.writer(failure_file)

cluster = range(1,N+1)
reinsert = list()
# Write header
row = ('time','machine','event')
writer.writerow(row)

print cluster
#Event generation loop
for t in list(np.arange(0,T,mttf)):
    n = random.randint(1,N)
    choice = np.random.choice([True, False], p= [prf,1-prf ])
#    print n
    if choice:
        if n+rack > N:
            for i in range(0,rack):
                node = cluster[n-i-1]
#   		print node
                reinsert.append(node)
                row = (t, n-i,1)
#		print row
                writer.writerow(row)
        else:
            for i in range(0, rack):
		node= cluster[n+i-1]
 #               print node
		reinsert.append(node)
                row = (t, n+i,1)
#		print row
                writer.writerow(row)
 	cluster = list(np.delete(cluster,reinsert))
#        print cluster
    else:
        reinsert.append(cluster.pop(n-1))
        row = (t,n,1)
#	print row
        writer.writerow(row)
    
#    print reinsert 
    for e in reinsert:
        bisect.insort(cluster, e)
        row = (t+1, e, 2)
        writer.writerow(row)    
    
    reinsert = list()
    
failure_file.close()

