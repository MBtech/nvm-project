from tasks import start_task, kill_task
from celery.messaging import establish_connection
import time
import pandas as pd
import time_between_failures as tf
import random

parent_dir = 'google-cluster/'
prob = 0.8 # Probability of predicting failure correctly

def cause_failure():
    events = pd.read_csv(parent_dir + 'norm_machine_events.csv')
    faults = events[events['event'] ==1]
    t = tf.next_failure_list()
    for i in range(0, len(t)):
        print faults.iloc[i]['machine']
        time.sleep(t[i])


def do_insertion():
   events = pd.read_csv(parent_dir + 'norm_machine_events.csv')
   additions = events[events['event'] == 0]
   t = tf.next_insert_list()
   for i in range(0,len(t)):
       print addtions.iloc[i]['machine']
       time.sleep(t[i])


# Create the initial process number to node mapping
def create_cluster(n,p):
    process_to_node = dict()
    for i in range(1,p+1):
        process_to_node[i] = i % n
    return process_to_node

# Start up the process to create initial 
def setup_cluster(process_to_node):
    process_to_pid = dict()
    pid_to_tid = dict()
    tid = 0 #task id: used as filename for snapshots

    for i in process_to_node.keys():
        res = start_task.apply_async(queue= 'node'+str(process_to_node[i]))
        while(not res.ready()):
            time.sleep(1)
        pid = res.get()
        process_to_pid[i] = pid
        pid_to_tid[pid] = tid        
        tid +=1
    return process_to_pid, pid_to_tid

# Kill a process caused by failure
def kill_process(process, process_to_pid):
    if process in process_to_pid.keys():
        kill_task(process_to_pid[process])

#Issue the instruction to take the snapshot of the process state
def take_snapshot(process, process_to_pid, pid_to_tid):
    if process in process_to_pid.keys():
        pid = process_to_pid[process]
        if pid in pid_to_tid.keys():
            take_snapshot(pid_to_tid[pid])

    
# TODO: Make use of this function for proper testing
# Main function to start the test
def start_test():
    n = 3
    p  = 10000
    process_to_node = create_cluster(n, p)
    process_to_pid, pid_to_tid = setup_cluster(process_to_node)
    
    #TODO: Failure loop
    while(1):
        #TODO: Get Failure
        kill_process(process, process_to_pid)
        res = numpy.random.choice([True, False], p=[prob, 1-prob])
        if res:
            take_snapshot(process, process_to_pid, pid_to_tid)
    


# Currently this code is directly testing the tasks.py code
if __name__ == "__main__":
    
    with establish_connection() as connection:
        for i in range(1,3):
            res = start_task.apply_async(connection=connection, queue= 'node'+str(i))
            while (not res.ready()):
                time.sleep(1)
            print res
            kill_task(res.get())

