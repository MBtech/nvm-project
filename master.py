from tasks import start_task, kill_task
from celery.messaging import establish_connection
import time

# Create the initial process number to node mapping
def create_cluster(n,p):
    process_to_node = dict()
    for i in range(1,p+1):
        process_to_node[i] = i % n
    return process_to_node

# Start up the process to create initial 
def setup_cluster(process_to_node):
    process_to_pid = dict()
    for i in process_to_node.keys():
        res = start_task.apply_async(queue= 'node'+str(process_to_node[i]))
        while(not res.ready()):
            time.sleep(1)
        process_to_pid[i] = res.get()
    return process_to_pid

# Kill a process caused by failure
def kill_process(process, process_to_pid):
    if process in process_to_pid.keys():
        kill_task(process_to_pid[process])
    
if __name__ == "__main__":
    
    with establish_connection() as connection:
        for i in range(1,3):
            res = start_task.apply_async(connection=connection, queue= 'node'+str(i))
            while (not res.ready()):
                time.sleep(1)
            print res
            kill_task(res.get())

