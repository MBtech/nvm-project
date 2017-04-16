import pandas as pd

events = pd.read_csv('machine_events.csv')

mapping = dict()
hash_ids = events[events['time']==0]['machine']
i = 0
for e in hash_ids:
    mapping[e] = i
    i +=1

print i
d = list()
other_events = events[(events['time'] > 0) & ((events['event']==1) | (events['event']==0))]
for x in range(0,len(other_events)):
    hash_id = other_events.iloc[x,1]
    if hash_id in mapping.keys():
        other_events.iloc[x,1] = mapping[hash_id] 
    else:
        d.append(x)
#        other_events.iloc[x,1] = i
#        i +=1

    
other_events.drop(other_events.index[d])
other_events.to_csv('norm_machine_events.csv')
 
#print other_events
print len(d)
