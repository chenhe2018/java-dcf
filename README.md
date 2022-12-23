# java-dcf

现金流试算，discounted cash flow，通过指定预设收益率，将未来的现金流<date,cash>映射到当前日期，即未来日期的现金流金额映射至当日是要变小。

[在线公式展示](https://www.latexlive.com/)

$$
PV=\sum_{i=0}^N\frac{C_i}{(1+y)^\frac{(d_i-d_1)}{T}}
$$

上述公式正向求解较为简单，但是反向求解较为复杂。因其为一元方程，故采用牛顿拉夫森算法进行求解。

## 参考

[java-irr](https://github.com/rockychen1221/java-irr)
