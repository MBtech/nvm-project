import random

def make_random_point(m,b):
    #y=mx+b
    min_x = -1000
    max_x = 1000
    x = random.randint(min_x, max_x)
    tmp_y = m*x+b
    var_y = 10 #how far can y deviate from real points
    y = tmp_y + random.randint(-var_y, var_y)

    return x,y

def write_points_for_file(filename, count, m, b):
    f = open(filename,"w") 

    for i in xrange(count):
        x,y = make_random_point(m,b)
        f.write(str(x) + " " + str(y))
        f.write("\n")

    f.close()


def write_all_points(filename_prefix, num_files, total_points, m, b):
    for i in xrange(num_files):
        write_points_for_file(filename_prefix+str(i), total_points/num_files, m, b)

#Inputs
#1) Filename prefix, if gd_points, files produced will be
#   gd_points0, gd_points1, and so on for the number of files
#2) How many files/machines
#3) How many points total
#4) m or slope in line
#5) b or y-intercept in a line
write_all_points("gd_points", 10, 400000,2,3)
