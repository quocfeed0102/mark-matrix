package enlan.ptithcm;

import java.util.*;

public class test {

    public static void main(String[] args) {
        // Tập văn bản đầu vào
        String[] documents = {
                "this be the first document",
                "this document be the second document",
                "and this be the third one"
        };

        // Xây dựng danh sách các từ
        List<String> wordsList = new ArrayList<>();
        for (String document : documents) {
            String[] words = document.split("\\s+");
            for (String word : words) {
                if (!wordsList.contains(word)) {
                    wordsList.add(word);
                }
            }
        }
        System.out.println("wordlist:" + wordsList);
        Collections.sort(wordsList);

        // Tính toán tf
        double[][] tf = new double[documents.length][wordsList.size()];
        for (int i = 0; i < documents.length; i++) {
            String[] words = documents[i].split("\\s+");
            for (int j = 0; j < wordsList.size(); j++) {
                int count = 0;
                for (String word : words) {
                    if (word.equals(wordsList.get(j))) {
                        count++;
                    }
                }
                tf[i][j] = (double) count / words.length;
            }
        }

        // Tính toán idf
        double[] idf = new double[wordsList.size()];
        Arrays.fill(idf, 0);
        for (int j = 0; j < wordsList.size(); j++) {
            for (int i = 0; i < documents.length; i++) {
                String[] words = documents[i].split("\\s+");
                for (String word : words) {
                    if (word.equals(wordsList.get(j))) {
                        idf[j]++;
                        break;
                    }
                }
            }
            idf[j] = Math.log(documents.length / idf[j]) / Math.log(2);
        }

        // Tính toán tf-idf
        double[][] tfidf = new double[documents.length][wordsList.size()];
        for (int i = 0; i < documents.length; i++) {
            for (int j = 0; j < wordsList.size(); j++) {
                tfidf[i][j] = tf[i][j] * idf[j];
            }
        }

        // In kết quả
        for (int i = 0; i < documents.length; i++) {
            System.out.printf("Document %d: ", i + 1);
            for (int j = 0; j < wordsList.size(); j++) {
                System.out.printf("%.4f ", tfidf[i][j]);
            }
            System.out.println();
        }
    }
}
