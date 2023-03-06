// N18DCC004 Hoang Nghia Quoc Anh
// N18DCCN163 Ho Mai Que
// N18DCCN166 Tran Anh Quoc

package enlan.ptithcm;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final Set<String> stopWords = new HashSet<String>(
            Arrays.asList(
                    "",
                    "a",
                    "is",
                    "the",
                    "of",
                    "all",
                    "to",
                    "can",
                    "be",
                    "as",
                    "once",
                    "for",
                    "at",
                    "am",
                    "are",
                    "has",
                    "have",
                    "had",
                    "up",
                    "his",
                    "her",
                    "in",
                    "on",
                    "no",
                    "we",
                    "do",
                    "by",
                    "or",
                    "and",
                    "not"
            )
    );

    private static final String fileDocumentPath = "doc.txt";

    private static final String fileQueryPath = "query.txt";

    private static final Map<String, Set<String>> documents = new HashMap<String, Set<String>>();

    private static final Map<String, Set<String>> queries = new HashMap<String, Set<String>>();

    private static final Map<String, Map<String, Integer>> markMatrix = new HashMap<String, Map<String, Integer>>();

    private static int[] query;

    private static Set<String> booleanAlgebra;

    private static String[] document;

    private static final String[] regexBoolean = {"AND", "OR", "AND NOT"};

    private static List<String> queryBooleanAfterProcess = new ArrayList<>();

    private static List<String> algebraBooleanAfterProcess = new ArrayList<>();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        readFile(documents, fileDocumentPath);
        readFile(queries, fileQueryPath);
        String choice;
        boolean isAnd = false;
        String optionalQuery = null;
        String indexQuery = null;
        do {

            System.out.println("1.Tim kiem chinh xac (AND)");
            System.out.println("2.Tim kiem tuong doi (OR)");
            System.out.println("3.Tim kiem theo query bat ky");
            System.out.println("4.Thoat");
            System.out.print("Nhap lua chon:");
            choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    isAnd = true;
                    System.out.print("Tim kiem theo cau query so:");
                    indexQuery = sc.nextLine();
                    break;
                }
                case "2": {
                    isAnd = false;
                    System.out.print("Tim kiem theo cau query so:");
                    indexQuery = sc.nextLine();
                    break;
                }
                case "3": {
                    System.out.println("Nhap menh de bat ky: ");
                    optionalQuery = sc.nextLine().trim();
                    initBooleanAlgebra(optionalQuery);
                    initAlgebra(optionalQuery);
                    break;
                }
                default: {
                    System.out.println("Nhap sai!");
                }
            }
            if (!(indexQuery == null)) {
                initMarkMatrix(indexQuery);
                initQuery(indexQuery);
                initDocument();
                System.out.println(queries.get(indexQuery));
                ArrayList<String> results = searchDocuments(
                        markMatrix,
                        document,
                        indexQuery,
                        isAnd
                );
                // Hiển thị kết quả tìm kiếm
                System.out.println("Results:");
                if (!results.isEmpty()) {
                    for (String document : results) {
                        System.out.println(document);

                    }
                }
                indexQuery = null;
            } else if (!(optionalQuery == null)) {
                System.out.println("optional");

                ///
                optionalQuery = null;

            }
        } while (!choice.equals("4"));

    }

    public static void initBooleanAlgebra(String input) {
        Pattern pattern = Pattern.compile("\\b(AND NOT|AND|OR)\\b");
        Matcher matcher = pattern.matcher(input);

        int start = 0;
        while (matcher.find()) {
            String token = input.substring(start, matcher.start()).trim();
            queryBooleanAfterProcess.add(token);
            start = matcher.end();
        }
    }

    public static void initAlgebra(String input) {
        for (String temp : queryBooleanAfterProcess) {
            input.replaceAll(temp, "");
        }
        Collections.addAll(algebraBooleanAfterProcess, input.trim().split(""));
    }

    public static void initMarkMatrix(String indexQuery) {
        for (int i = 1; i <= documents.size(); i++) {
            markMatrix.put("" + i, new HashMap<>());
            for (String keyword : queries.get(indexQuery)) {
                if (documents.get("" + i).contains(keyword)) {
                    markMatrix.get("" + i).put(keyword, 1);
                }
            }
        }
    }

    public static void initQuery(String index) {
        query = new int[queries.get(index).size()];
        for (int i = 0; i < queries.get(index).size(); i++) {
            query[i] = 1;
        }
    }

    public static void initDocument() {
        document = new String[documents.size()];
        for (int i = 1; i <= documents.size(); i++) {
            document[i - 1] = "" + i;
        }
    }

    public static ArrayList<String> searchDocuments(
            Map<String, Map<String, Integer>> markMatrix,
            String[] documents,
            String query,
            boolean isAnd
    ) {
        ArrayList<String> results = new ArrayList<String>();
        Map<String, Integer> sum = new HashMap<>();
        // Tính tổng của các hàng tương ứng với các từ khóa trong câu truy vấn
        for (String document : documents) {
            int querySum = 0;
            for (String keyword : markMatrix.get(document).keySet()) {
                if (markMatrix.get(document).get(keyword) == 1) {
                    querySum += 1;
                }
            }
            if (isAnd) {
                if (querySum == queries.size()) {
                    results.add(document);
                }
            } else {
                if (querySum > 0)
                    results.add(document);
            }
        }
        if (isAnd) return results;
//        int maxVal = sum.values().stream().max(Integer::compare).get();
//        results = (ArrayList<String>) sum.entrySet().stream()
//                .filter(entry -> entry.getValue() == maxVal)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());

        return results;
    }

//    public static ArrayList<String> searchDocuments(
//            Map<String, Map<String, Integer>> markMatrix,
//            String[] documents,
//            String query
//    ) {
//        String bl = "";
//        ArrayList<String> results = new ArrayList<String>();
//        Map<String, Integer> sum = new HashMap<>();
//        // Tính tổng của các hàng tương ứng với các từ khóa trong câu truy vấn
//        for (String document : documents) {
//            for (String tempQuery : queryBooleanAfterProcess) {
//                int querySum = 0;
//                bl = bl + tempQuery + " " + algebraBooleanAfterProcess.remove(0);
//                initMarkMatrix(tempQuery);
//                for (String keyword : markMatrix.get(document).keySet()) {
//                    if (querySum == queries.size()) {
//                        results.add(document);
//                    }
//                }
//            }
//
//        }
//        return results;
//    }

    public static void readFile(Map<String, Set<String>> map, String filePath) {
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            String temp = "";
            String[] temps;
            Set<String> tempSet;
            boolean flag = true;
            int index = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (flag) {
                    index = Integer.parseInt(line.trim());
                    flag = false;
                } else {
                    if (line.trim().equals("/")) {
                        flag = true;
                        map.put("" + index, npl(temp));
                        temp = "";
                    } else {
                        temp = temp + " " + line.trim().toLowerCase();
                    }
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> npl(String text) {
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create a document object
        CoreDocument document = pipeline.processToCoreDocument(text);
        Set<String> result = new HashSet<String>();
        String temp;
        // display tokens
        for (CoreLabel tok : document.tokens()) {
            temp = tok.lemma();
            if (!stopWords.contains(temp)) {
                result.add(temp.toLowerCase());
            }
        }
        return result;
    }
}
