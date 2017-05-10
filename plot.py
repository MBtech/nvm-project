import pandas
import sys
from pylab import *
import matplotlib as mpl

def setBoxColors(bp):
    setp(bp['boxes'][0], color='blue')

# Usage example: python perf_variance.py rollingcount
# Plots the graphs for the mis-configuration section
#

iterations = dict(list())
extensions = ['0.7', '0.8', '0.9', 'nop']
for e in extensions:
    with open('results/result_'+e) as f:
        lines = f.readlines()
        iterations[e] = map((lambda x: (1000000-x)/1000),map(int,lines))

print iterations
# first boxplot pair
fig = figure()
ax = axes()
hold(True)
mpl.rcParams['font.size']=18

bp = boxplot(latency['0.7'], positions = [1], widths = 0.6)
setBoxColors(bp)
bp = boxplot(latency['0.8'], positions = [3], widths = 0.6)
setBoxColors(bp)
bp = boxplot(latency['0.9'], positions = [5], widths = 0.6)
setBoxColors(bp)
bp = boxplot(latency['nop'], positions = [7], widths = 0.6)
setBoxColors(bp)


#boxplot(data)

xlim(0,8)
ax.set_xticklabels(['p=0.7', 'p=0.8', 'p=0.9', 'p=0'])
ax.set_xticks([1, 3, 5, 7])

ax.set_xlabel("Prediction accuracy (fraction of errors predicted)")
ax.set_ylabel("Average number of iterations completed (in thousands)")
fig.savefig("result.pdf")
#plt.title('Performance var')
show()
