from tasks import start_task, kill_task
from celery.messaging import establish_connection
import time
import pandas as pd
import random
import master
import numpy

parent_dir = ''
prob = 0.8

def kill(process, process_to_pid, process_to_node, pid_to_tid, node):
    #TODO: Get Failure
    #[process_to_pid,pid_to_tid, process_to_node] = master.get_all()
    res = numpy.random.choice([True, False], p=[prob, 1-prob])
    if res:
        print "Requesting a snapshot"
        master.take_snapshot(process, process_to_pid, process_to_node, pid_to_tid, node, process_to_node[process])
    n = process_to_node[process]    
    #This is the grace period that the fault predictor gives us before the actual failure occurs 
    time.sleep(5)
    print "Issuing the kill command for process " + str(process)
    master.kill_process(process, process_to_pid, process_to_node, pid_to_tid, node)
    return n

def start(process, process_to_pid, process_to_node, pid_to_tid, node, retrieval_node, state):
    #TODO: Add the code to start new processes
    master.start_process(process, process_to_pid, process_to_node, pid_to_tid, node, retrieval_node, state)
    print "Requesting startup on new processes"+ str(process)
    

def execute_events(process_to_pid, process_to_node, pid_to_tid, node):
    last_s = 0
    events = pd.read_csv(parent_dir + 'failures.csv')
    retrieval = dict()
    for i in range(0,len(events['event'])):
        s = events.iloc[i]['time']
        print "Sleeping for "+str(s)+" seconds"
        time.sleep(s-last_s)
        if int(events.iloc[i]['event']) == 1:
            
            retrieval[int(events.iloc[i]['machine'])] = kill(int(events.iloc[i]['machine']), process_to_pid, process_to_node, pid_to_tid, node)
        else:
            start(int(events.iloc[i]['machine']), process_to_pid, process_to_node, pid_to_tid, node, retrieval[int(events.iloc[i]['machine'])], 1)
            del retrieval[int(events.iloc[i]['machine'])]
        last_s = s

