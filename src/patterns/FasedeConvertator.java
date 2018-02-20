/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patterns;

import java.io.File;

/**
 *
 * @author danii
 */
public class FasedeConvertator {
    /**
     *Клиентский код демонстрации VideoConversionFacade
     * <p>Определяет {@link VideoConversionFacade} и конвертирует файл, вся реализция скрыта
     */
    static class Client{
        public static void main(String[] args){
           VideoConversionFacade converted = new VideoConversionFacade();
           File mp4Video = converted.convertVideo("youtubevideo.ogg","mp4");
        }
}
    
}

  class VideoFile{
      private String name;
      private String codecType;
      
      public VideoFile(String name){
          this.name=name;
          this.codecType = name.substring(name.indexOf(".")+1, name.length());
      }
      public String getCodecType(){ return codecType;}
      
      public String getName(){return name;}
  }

interface Codec{}

class MPEG4ComressionCodec implements Codec{
    public String type="mp4";
}

class OggComressionCodec implements Codec{
    public String type="ogg";
}
// Фабрика видеокодеков кодеков
 class CodecFactory{
    public static Codec extract(VideoFile file){
        String type= file.getCodecType();
        if(type.equals("mp4")){
            System.out.println("CodecFactory: extracting mpeg audio...");
            return new MPEG4ComressionCodec();
        }
        else {
            System.out.println("CodecFactory: extracing ogg audio...");
            return new OggComressionCodec();
        }
    }
}
//Bitrate - конверетр
class BitrateReader{
    public static VideoFile read(VideoFile file, Codec codec){
        System.out.println("BirtrateReader: reading file...");
        return file;
    }
    public static VideoFile convert(VideoFile buffer, Codec codec){
        System.out.println("BirtrateReader: writing file...");
        return buffer;
    }
    
}

class AudioMixed{
    public static File fix(VideoFile result){
        System.out.println("AudioMixed: fixed audio....");
        return new File("tmp");
    }
}

/// Фасад библиотеки работы с видео
class VideoConversionFacade{
    public File convertVideo(String fileName, String format){
        System.out.println("VideoConversionFacade: conversion started...");
        VideoFile file = new VideoFile(fileName);
        Codec sourceCodec=CodecFactory.extract(file);
        Codec destionCodec;
        if(format.equals("mp4"))
            destionCodec = new OggComressionCodec();
        else 
            destionCodec=new MPEG4ComressionCodec();
        
        VideoFile buffer = BitrateReader.read(file,sourceCodec);
        VideoFile intermediateResult=BitrateReader.convert(buffer,destionCodec);
        File result =(new AudioMixed()).fix(intermediateResult);
        System.out.println("VideoConversionFasade: conversion complected.");
        return result;
    }
}


