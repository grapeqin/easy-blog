
# LeetCode 5 : 最长回文子串

## 问题描述

**给定一个字符串s，找到s中最长的回文子串，若最长的回文子串有多个，则返回s中起始下标最大的回文子串**

**示例1：**

**输入：** ""

**输出：** ""

**分析：** 因为空字符串本身就是回文，所以直接返回其自身

**示例2：**

**输入：** "a"

**输出：** "a"

**分析：** 因为单字符串本身就会回文，所以直接返回其自身

**示例3：**

**输入：** "babad"

**输出：** "aba"

**分析：** 因为最长回文子串有2个，"bab"和"aba"，但"aba"的起始下标为1，比"bab"的起始下标0大，所以返回"aba"

**示例4：**

**输入：** "cbbd"

**输出：** "bb"

**分析：** 因为最长回文为"bb"，所以直接返回"bb"

## 分析过程及实现

### 1. 思路

*  回文定义：回文即顺读和反读都一样的字符串。
* 对于字符串s,假设s[i,j]为中心存在子串，我们可以根据回文的对称性，以s[i] == s[j] 为判断标准依次从中心向两侧扩展开，直到不再满足回文定义则终止循环，然后就可以获得以s[i,j]为中心的回文子串
*  源码演示
```java
/**  
* 返回s[i]和s[j]为回文中心最长回文子串的长度 
* @param s  原始字符串s
* @param i  回文的中心子串起始下标
* @param j  回文的中心子串终止下标
* @return  回文长度
*/  
private int longestPalindromeGap(String s,int i,int j){  
	int start = i;  
	int end = j;  
	while (start >= 0 
			&& end < s.length() 
			&& s.charAt(start) == s.charAt(end)){  
		start--;  
		end++;  
	}  
	return end-start-1;  
}
```
* 对字符串s,假设其长度为n(n>0),在任意位置i[0,n)，都存在两种可能性，要么以s[i]为中心存在回文子串，要么以s[i]和s[i+1]为中心存在回文子串，对于最后一个字符s[n-1]，就只有一种可能即以s[n-1]为中心存在回文子串。总的来说，长度为n的字符串s，存在回文子串的可能性有2n-1种。 
* 源码如下：
```java
public String longestPalindrome2(String s){  
	if(null==s || s.length() < 1) {
		return ""; 
	} 
	int start = 0;  
	int end = 0;  
	int odd = 0;  
	int even = 0;  
	int length = 0;  
	for(int i=0;i<s.length();i++){  
		odd = longestPalindromeGap(s,i,i);  
		even = longestPalindromeGap(s,i,i+1);  
		length = Math.max(odd,even);  
		if(end-start < length){  
			start = i-(length-1)/2;  
			end = i+length/2;  
		}  
	}  
	return s.substring(start,end+1);  
}
```