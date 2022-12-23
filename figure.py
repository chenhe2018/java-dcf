import matplotlib.pyplot as plt
import numpy as np

data = np.loadtxt("dcf-positive.txt")

# plt.plot(data[:,0],data[:,1])
# plt.plot(data[:,0],data[:,2])
plt.plot(data[:,0],data[:,2])


plt.show()