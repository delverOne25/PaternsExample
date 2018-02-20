/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patterns;

import java.io.*;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
/**
 *
 * @author danii
 */
public class CompressionDecorator  {
    
    
    
    
    
    
   
    public static interface DataSource{
        void writeData(String data);
        String readData();
    }
    
    public static class FileDataSource implements DataSource{
        private String name;
        
        public FileDataSource(String name){
            this.name=name;
        }
        @Override
        public void writeData(String data){
            File file =new File(name);
            try(OutputStream fos =new FileOutputStream(file)){
                fos.write(data.getBytes(),0,data.length());
            }catch(IOException ex){ex.printStackTrace();}
                
        }
        @Override 
        public String readData(){
            char[] buffer=null;
            File file=new File(name);
            try(FileReader reader=new FileReader(file)){
                buffer =new char[(int)file.length()];
                reader.read(buffer);
            }catch(IOException ex){ex.printStackTrace();}
            
            return new String(buffer);
        }
    }
    public static class DataSourseDecorator implements DataSource{
        private DataSource wrappee;
        public DataSourseDecorator(DataSource source){
             this.wrappee=source;
        }
        public void writeData(String str){
            wrappee.writeData(str);
        }
        public String readData(){
            return wrappee.readData();
        }
        
    }
    
    public static class EncryptionDecorator extends DataSourseDecorator{
        
        public EncryptionDecorator(DataSource source){
            super(source);    
        }
        public void writeData(String data){
            super.writeData(encode(data));
        }
        public String readData(){
            return decode(super.readData());
        }   
        private String encode(String data){
            byte[] result =(data).getBytes();
            for(int i=0;i<result.length; i++){
                result[i]+=(byte)1;
            }
            return Base64.getEncoder().encodeToString(result);
        }
        private String decode(String data){
            byte []result= Base64.getDecoder().decode(data);
            for(int i=0; i<result.length;i++)
                result[i]-=(byte)1;
            return new String(result);
        }
     
     
    }
    public static class CompressionDecorator1 extends DataSourseDecorator {
    
        private int compLevel =6;
        public CompressionDecorator1(DataSource sourse){
            super(sourse);
        }
        public int getCompressionLevel(){return compLevel;}
        public void setCompressionLevel(int l){compLevel=l;}
        @Override
        public void writeData(String data){
            super.writeData(compress(data));
        }
        @Override
        public String readData(){
            return decompress(super.readData());
        }
           public String compress(String stringData) {
        byte[] data = stringData.getBytes();
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            DeflaterOutputStream dos = new DeflaterOutputStream(bout, new Deflater(compLevel));
            dos.write(data);
            dos.close();
            bout.close();
            return Base64.getEncoder().encodeToString(bout.toByteArray());
        } catch (IOException ex) {
            return null;
        }
    }

        public String decompress(String stringData) {
            byte[] data = Base64.getDecoder().decode(stringData);
            try {
                InputStream in = new ByteArrayInputStream(data);
                InflaterInputStream iin = new InflaterInputStream(in);
                ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
                int b;
                while ((b = iin.read()) != -1) {
                    bout.write(b);
                }
                in.close();
                iin.close();
                bout.close();
                return new String(bout.toByteArray());
            } catch (IOException ex) {ex.printStackTrace();
                return null;
            }
        }

        }
    public static class Demo{
        public static void main(String []args){
                    String salaryRecords = "Mame,Salary\nJohn Smith,100000\nSteven Jobs,912000";
        DataSourseDecorator encoded = new CompressionDecorator1(
                                         new EncryptionDecorator(
                                             new FileDataSource("out/OutputDemo.txt")));
        encoded.writeData(salaryRecords);
        DataSource plain = new FileDataSource("out/OutputDemo.txt");

        System.out.println("- Input ----------------");
        System.out.println(salaryRecords);
        System.out.println("- Encoded --------------");
        System.out.println(plain.readData());
        System.out.println("- Decoded --------------");
        System.out.println(encoded.readData());



        }
    }
        
    
    
}
