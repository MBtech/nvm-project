from tasks import start_task, kill_task,snapshot_task
from celery.messaging import establish_connection
import time
import pandas as pd
#import time_between_failures as tf
import random
import failure_gen
import sys

parent_dir = 'google-cluster/'
prob = 0.8 # Probability of predicting failure correctly
process_to_pid = dict()
process_to_node = dict()
pid_to_tid = dict()
node = dict()
tid = 0 #task id: used as filename for snapshots

## Getter methods
def get_process_to_pid():
    return process_to_pid

def get_process_to_node():
    return process_to_node

def get_pid_to_tid():
    return pid_to_tid

def get_all():
    return process_to_pid, pid_to_tid, process_to_node

# Create the initial process number to node mapping
def create_cluster(n,p):
    global node
    for i in range(1,p+1):
        process_to_node[i] = i % n
        node[i%n].append(i)

    print node
    return process_to_node, node

# Start up the process to create initial 
def setup_cluster(process_to_node):
    global tid 
    for i in process_to_node.keys():
        res = start_task.apply_async(queue= 'node'+str(process_to_node[i]), args=(str(tid),tid,-1,0))
        while(not res.ready()):
            time.sleep(1)
        pid = res.get()
        process_to_pid[i] = pid
        pid_to_tid[pid] = tid        
        tid +=1
    return process_to_pid, pid_to_tid

# Kill a process caused by failure
def kill_process(process, process_to_pid, process_to_node, pid_to_tid, node):
    print process
    print process_to_pid
    print process_to_node
    print pid_to_tid
    print node
    if process in process_to_pid.keys():
        kill_task.apply_async(queue='node'+str(process_to_node[process]), args=(pid_to_tid[process_to_pid[process]],process_to_pid[process]))
        print "Requesting Worker to kill a process"
        node[process_to_node[process]].remove(process) # Remove the node mapping from list
        del pid_to_tid[process_to_pid[process]]
        del process_to_node[process]
        del process_to_pid[process]
        #TODO: Removal from other lists? 


#Issue the instruction to take the snapshot of the process state
def take_snapshot(process, process_to_pid, process_to_node, pid_to_tid, node, retrieval_node):
    if process in process_to_pid.keys():
        pid = process_to_pid[process]
        if pid in pid_to_tid.keys():
            snapshot_task.apply_async(queue='node'+str(process_to_node[process]), args=(pid_to_tid[pid],retrieval_node))

#Get node with least processes
def get_pref_node(node):
    load = sorted(node, key= lambda k: len(node[k]), reverse=True)
    print load
    return load[-1] 

def assign_node(process, process_to_node, node):
    n  = get_pref_node(node)
    node[n].append(process)
    process_to_node[process] = n
    return process_to_node

def start_process(process, process_to_pid, process_to_node, pid_to_tid, node, retrieval_node,state): 
    assign_node(process, process_to_node,node)
    res = start_task.apply_async(queue='node'+str(process_to_node[process]), args=(str(process-1), process-1,retrieval_node,state))
    while(not res.ready()):
        time.sleep(1)
    pid = res.get()
    process_to_pid[process] = pid
    pid_to_tid[pid] = process-1

# TODO: Make use of this function for proper testing
# Main function to start the test
def start_test(n, prob):
    p  = 9
    
    process_to_node, node = create_cluster(n, p)
    print process_to_node
    process_to_pid, pid_to_tid = setup_cluster(process_to_node)
    
    failure_gen.execute_events(process_to_pid, process_to_node, pid_to_tid, node, prob)
    #TODO: Failure loop
    #while(1):
        #TODO: Get Failure
        #kill_process(process, process_to_pid)
        #res = numpy.random.choice([True, False], p=[prob, 1-prob])
        #if res:
        #    take_snapshot(process, process_to_pid, pid_to_tid)
    


# Currently this code is directly testing the tasks.py code
if __name__ == "__main__":
    n = 1
    global node
    prob = float(sys.argv[1])
    for i in range(0, n):
        node[i] = list()

    start_test(n, prob)

