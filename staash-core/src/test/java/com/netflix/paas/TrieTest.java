/*******************************************************************************
 * /***
 *  *
 *  *  Copyright 2013 Netflix, Inc.
 *  *
 *  *     Licensed under the Apache License, Version 2.0 (the "License");
 *  *     you may not use this file except in compliance with the License.
 *  *     You may obtain a copy of the License at
 *  *
 *  *         http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *     Unless required by applicable law or agreed to in writing, software
 *  *     distributed under the License is distributed on an "AS IS" BASIS,
 *  *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *     See the License for the specific language governing permissions and
 *  *     limitations under the License.
 *  *
 ******************************************************************************/
package com.netflix.paas;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TrieTest {
    public static interface TrieNodeVisitor {
        void visit(TrieNode node);
    }
    
    public static interface TrieNode {
        public TrieNode getOrCreateChild(Character c);
        public TrieNode getChild(Character c);
        public Character getCharacter();
        public void setIsWord(boolean isWord);
        public boolean isWord();
        public void accept(TrieNodeVisitor visitor);
    }
    
    public static class HashMapTrieNode implements TrieNode {
        private final ConcurrentMap<Character, TrieNode> children = Maps.newConcurrentMap();
        private final Character character;
        private volatile boolean isWord = false;
        
        public HashMapTrieNode(Character ch) {
            this.character = ch;
        }
        
        public TrieNode getChild(Character c) {
            return children.get(c);
        }
        
        public TrieNode getOrCreateChild(Character c) {
            TrieNode node = children.get(c);
            if (node == null) {
                TrieNode newNode = new HashMapTrieNode(c);
                System.out.println("Create child : " + c);
                node = children.putIfAbsent(c, newNode);
                if (node == null)
                    return newNode;
                    
            }
            return node;
        }

        public void setIsWord(boolean isWord) {
            this.isWord = isWord;
        }
        
        public boolean isWord() {
            return isWord;
        }
        
        @Override
        public Character getCharacter() {
            return this.character;
        }
        
        public void accept(TrieNodeVisitor visitor) {
            List<TrieNode> nodes = Lists.newArrayList(children.values());
            Collections.sort(nodes, new Comparator<TrieNode>() {
                @Override
                public int compare(TrieNode arg0, TrieNode arg1) {
                    return arg0.getCharacter().compareTo(arg1.getCharacter());
                }
            });
            for (TrieNode node : nodes) {
                visitor.visit(node);
            }
        }
    }        
    
    public static class AtomicTrieNode implements TrieNode {
        private final AtomicReference<Map<Character, TrieNode>> children = new AtomicReference<Map<Character, TrieNode>>();
        private final Character character;
        private volatile boolean isWord = false;
        
        public AtomicTrieNode(Character ch) {
            this.children.set(new HashMap<Character, TrieNode>());
            this.character = ch;
        }
        
        public TrieNode getChild(Character c) {
            return children.get().get(c);
        }
        
        public TrieNode getOrCreateChild(Character c) {
            TrieNode node = children.get().get(c);
            if (node == null) {
                Map<Character, TrieNode> newChs;
                do {
                    Map<Character, TrieNode> chs = children.get();
                    node = chs.get(c);
                    if (node != null) {
                        break;
                    }
                    
                    newChs = Maps.newHashMap(chs);
                    node = new AtomicTrieNode(c);
                    newChs.put(c, node);
                    if (children.compareAndSet(chs,  newChs)) {
                        break;
                    }
                }
                while (true);
            }
            return node;
        }

        public void setIsWord(boolean isWord) {
            this.isWord = isWord;
        }
        
        public boolean isWord() {
            return isWord;
        }
        
        @Override
        public Character getCharacter() {
            return this.character;
        }
        
        public void accept(TrieNodeVisitor visitor) {
            List<TrieNode> nodes = Lists.newArrayList(children.get().values());
            Collections.sort(nodes, new Comparator<TrieNode>() {
                @Override
                public int compare(TrieNode arg0, TrieNode arg1) {
                    return arg0.getCharacter().compareTo(arg1.getCharacter());
                }
            });
            for (TrieNode node : nodes) {
                visitor.visit(node);
            }
        }
    }    
    
    public static class Trie {
        private TrieNode root = new AtomicTrieNode(null);
        
        public boolean addWord(String word) {
            word = word.toUpperCase();
            
            StringCharacterIterator iter = new StringCharacterIterator(word);
            TrieNode current = root;
            for (Character ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
                current = current.getOrCreateChild(ch);
            }
            current.setIsWord(true);
            return true;
        }
        
        public boolean containsWord(String word) {
            word = word.toUpperCase();
            StringCharacterIterator iter = new StringCharacterIterator(word);
            TrieNode current = root;
            for (Character ch = iter.first(); ch != CharacterIterator.DONE; ch = iter.next()) {
                current = current.getChild(ch);
                if (current == null)
                    return false;
            }
            return current.isWord();
        }
        
        public void accept(TrieNodeVisitor visitor) {
            visitor.visit(root);
        }
        
    }
    
    public static class SimpleTriePrinter implements TrieNodeVisitor {
        private String prefix = "";
        
        @Override
        public void visit(TrieNode node) {
            System.out.println(prefix + node.getCharacter());
            prefix += " ";
            node.accept(this);
            prefix = StringUtils.substring(prefix,  1);
        }
        
    }
    
    @Test
    public void testTrie() {
        String[] common = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};
        
        Trie trie = new Trie();
        for (String word : common) {
            trie.addWord(word);
        }
        
        trie.accept(new SimpleTriePrinter());
    }
}
