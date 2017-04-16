import time 
import pandas as pd
import time_between_failures as tf 
 
def cause_failure():
    events = pd.read_csv('norm_machine_events.csv')
    faults = events[events['event'] ==1]
    t = tf.next_failure_list()
    for i in range(0, len(t)):
        print faults.iloc[i]['machine']
        time.sleep(t[i])   


def do_insertion():
   events = pd.read_csv('norm_machine_events.csv') 
   additions = events[events['event'] == 0]
   t = tf.next_insert_list()
   for i in range(0,len(t)):
       print addtions.iloc[i]['machine']
       time.sleep(t[i])
    

cause_failure()
do_insertion()
