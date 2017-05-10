from celery import Celery
import time
import random
import os, signal
import subprocess
import shutil

app = Celery('tasks', backend='amqp',broker='amqp://mb:guest@mcnode01/host')
pdir = 'files/'

@app.task
def cleanup():
    shutil.rmtree(pdir)

@app.task
def start_task(tid, snapshot_file, retrieval_node, state):
    if os.path.exists(str(tid)):
        os.remove(str(tid))
    print "Starting the programs"
    proc = subprocess.Popen(['java', '-jar','./RMI/RMI_Client.jar', tid, 'snapshot_'+ str(snapshot_file), str(retrieval_node), str(state), '/home/ubuntu/bilal/nvm-project/', './RMI/RMIClientSide/RMI_Experiment/Client/gd_points', '&'])
    print "PID:", proc.pid
    #print "Return code:", proc.wait()
    return proc.pid

@app.task
def kill_task(tid, pid):
    proc = subprocess.Popen(['kill', '-9', str(pid)])
    print "kill the process " + str(pid)


@app.task
def snapshot_task(tid, retrieval_node):
#    if not os.path.exists(pdir):
#        os.makedirs(pdir)
    target = open(str(tid), 'w')
    target.write(str(retrieval_node))
    print "Snapshot requested"
    
