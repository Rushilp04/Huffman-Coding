package huffman;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
	    /* Your code goes here */
        sortedCharFreqList = new ArrayList<CharFreq>();
        int[] x = new int [128];
        int counter = 0;
        while(StdIn.hasNextChar()){
            x[StdIn.readChar()]++;
            counter++; // counter has the number of characters in the file
        }
        int one =0;
        for(int i = 0; i < 128; i++){
            if(x[i] != 0){
                CharFreq newChar = new CharFreq((char)i, (double)x[i]/counter);
                sortedCharFreqList.add(newChar);
                one = i+1;
            }
        }
        CharFreq y;
        if(sortedCharFreqList.size() == 1){
            if(one == 128){
                y = new CharFreq((char)0, 0.0);
            }
            else{
                y = new CharFreq((char)one, 0.0);
            }
            sortedCharFreqList.add(y);
        }
        Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public TreeNode smallest (Queue<TreeNode> source, Queue<TreeNode> target){
        if(target.isEmpty() && !(source.isEmpty())){
            return source.dequeue();
        }
        if(source.isEmpty()){
            return target.dequeue();
        }
        if(source.peek().getData().getProbOcc() <= target.peek().getData().getProbOcc()){
            return source.dequeue();
        }
        if(source.peek().getData().getProbOcc() > target.peek().getData().getProbOcc()){
            return target.dequeue();
        }
        return source.dequeue();
    }
    public void makeTree() {
	    /* Your code goes here */

        Queue<TreeNode> source = new Queue<TreeNode>();
        Queue<TreeNode> target = new Queue<TreeNode>();
        TreeNode val1 = new TreeNode();
        TreeNode val2 = new TreeNode();
        for(int i = 0; i < sortedCharFreqList.size();i++){
            TreeNode first = new TreeNode(sortedCharFreqList.get(i), null, null);
            source.enqueue(first);
        }
        while(!source.isEmpty() || target.size() > 1){
            val1 = smallest(source, target);
            val2 = smallest(source, target);
            double sum = (val1.getData().getProbOcc())+(val2.getData().getProbOcc());
            CharFreq val3 = new CharFreq(null, sum);
            TreeNode node = new TreeNode(val3, val1 , val2);
            target.enqueue(node);
        }
        huffmanRoot = target.peek();
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    /*
    Iteratively
    public String code(TreeNode root){
        String x = "";
        if((root.getLeft().getData().getCharacter()) == null){
            x += "0";
        }
        return "0";
    }
    */
    public void code (TreeNode root, String encoding){        
        if(root.getLeft() != null){
            code(root.getLeft(), encoding + "0");
        }
        if((root.getRight()) != null){
            code(root.getRight(), encoding + "1");
        }
        if(root.getData().getCharacter() != null){
            encodings[root.getData().getCharacter()] = encoding;
        }
    }
    public void makeEncodings() {
	    /* Your code goes here */
        String encoding = "";
        encodings = new String [128];
        
        if (huffmanRoot.getLeft() == null && huffmanRoot.getRight() == null) {
            encodings[huffmanRoot.getData().getCharacter()] = "0";
        }else{
            code(huffmanRoot, encoding);
        }
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
        String s = "";
        while (StdIn.hasNextChar()) {
            s += encodings[StdIn.readChar()];
        }
        writeBitString(encodedFile, s);
	    /* Your code goes here */
        
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
	    /* Your code goes here */
        String str = readBitString(encodedFile);
        //readBitString(decodedFile);
        boolean[] value = new boolean[str.length()];
        //StdOut.println(str);
        int x = str.length();
        for(int i = 0; i < x; i++){
            if(str.substring(0,1).equals("0")){
                value[i]= false;
            }
            else{
                value[i] = true;
            }
            str = str.substring(1);
        }
        //StdOut.println(str);

        TreeNode root = huffmanRoot;
        for(int i = 0; i < value.length; i++){
            if(value[i]){
                if(!(root.getRight().getData().getCharacter() == null)){
                    StdOut.print(root.getRight().getData().getCharacter());
                    root = huffmanRoot;
                }
                else{
                    root = root.getRight();
                }
            }
            else{
                if(!(root.getLeft().getData().getCharacter() == null)){
                    StdOut.print(root.getLeft().getData().getCharacter());
                    root = huffmanRoot;
                }
                else{
                    root = root.getLeft();
                }
            }
        }
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
