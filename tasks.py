from celery import Celery
import time
import random
import os, signal
import subprocess

app = Celery('tasks', backend='amqp',broker='amqp://mb:guest@mcnode01/host')
pdir = 'files/'

@app.task(ignore_result=True)
def get_time():
    print time.time()

@app.task
def gen_prime(x):
    multiples = []
    results = []
    for i in range(2,x+1):
        if i not in multiples:
            results.append(i)
            for j in xrange(i*i, x+1, i):
                multiples.append(j)
    return results

@app.task
def start_task(tid):
    print "Starting the programs"
    proc = subprocess.Popen(['java', '-jar','./hello.jar', tid])
    print "PID:", proc.pid
    #print "Return code:", proc.wait()
    return proc.pid

@app.task
def kill_task(tid, pid):
    os.remove(pdir+tid)
    proc = subprocess.Popen(['kill', '-9', str(pid)])
    print "kill the process " + str(pid)


@app.task
def take_snapshot(tid):
    target = open(pdir+tid, 'w')
    target.write('fail')
    
