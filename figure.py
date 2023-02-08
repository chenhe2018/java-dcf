import matplotlib.pyplot as plt
import numpy as np


#导入数据
data = np.loadtxt('dcf.txt',delimiter='\t')


#开始画图
plt.title('Result Analysis')
# plt.plot(data[:,0], data[:,1], color='green', label='training accuracy')
plt.plot(data[:,0],data[:,2], color='red', label='testing accuracy')
# plt.plot(x_axix, train_pn_dis,  color='skyblue', label='PN distance')
# plt.plot(x_axix, thresholds, color='blue', label='threshold')
plt.legend() # 显示图例

plt.xlabel('iteration times')
plt.ylabel('rate')
plt.show()