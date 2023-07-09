package org.scuvis.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤类
 *
 * @author Xiyao Li
 * @date 2023/06/15 14:42
 */
@Component
public class SensitiveFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){

        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = br.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        }catch (IOException e){
            LOGGER.error("加载敏感词文件失败: " + e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode trieNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            if(trieNode.getSubNode(c) == null) {
                trieNode.addSubNode(c);
            }
            trieNode = trieNode.getSubNode(c);
            if(i == keyword.length() - 1){
                trieNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤的核心代码
     *
     * @param text 输入的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
       if(StringUtils.isBlank(text)){
           return null;
       }
       TrieNode p1 = rootNode;
       int p2 = 0;
       int p3 = 0;

       StringBuilder sb = new StringBuilder();


       while(p2 < text.length()){
           if(p3 < text.length()){
               Character c = text.charAt(p3);
               // 如果p3处是符号
               if(isSymbol(c)){
                   // 且p1是根节点
                   if(p1 == rootNode){
                       //p2可以直接向后移动，p3加入到sb里
                       p2++;
                       sb.append(c);
                   }
                   // 无论p1是不是根节点，都要把p3右移一位
                   p3++;
                   continue;
               }

               p1 = p1.getSubNode(c);
               if(p1 == null){
                   // 证明不是敏感词
                   sb.append(text.charAt(p2));
                   p3 = ++p2;
                   p1 = rootNode;
               }else if(p1.isKeyWordEnd){
                   // 找到敏感词
                   sb.append(REPLACEMENT);
                   p2 = ++p3;
                   p1 = rootNode;
               }else{
                   p3++;
               }
           }
       }
       sb.append(text.substring(p2));
       return sb.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    public static class TrieNode{
        boolean isKeyWordEnd = false;

        // 保存子节点
        Map<Character,TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public void addSubNode(Character c){
            subNodes.put(c,new TrieNode());
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
