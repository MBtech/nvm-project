import pandas as pd


def next_failure_list():
    events = pd.read_csv('norm_machine_events.csv')

    last = list()
    last_time = 0
    for i in range(0, len(events)):
        if events.iloc[i]['event'] == 1:
            last.append((events.iloc[i]['time'] - last_time) / 1000000)
            last_time = events.iloc[i]['time']

    return last


def next_insert_list():
    events = pd.read_csv('norm_machine_events.csv')

    last = list()
    last_time = 0
    for i in range(0, len(events)):
        if events.iloc[i]['event'] == 0:
            last.append((events.iloc[i]['time'] - last_time) / 1000000)
            last_time = events.iloc[i]['time']

    return last
