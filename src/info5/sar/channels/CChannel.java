/*
 * Copyright (C) 2023 Pr. Olivier Gruber                                    
 *                                                                       
 * This program is free software: you can redistribute it and/or modify  
 * it under the terms of the GNU General Public License as published by  
 * the Free Software Foundation, either version 3 of the License, or     
 * (at your option) any later version.                                   
 *                                                                       
 * This program is distributed in the hope that it will be useful,       
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         
 * GNU General Public License for more details.                          
 *                                                                       
 * You should have received a copy of the GNU General Public License     
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package info5.sar.channels;


import info5.sar.utils.CircularBuffer;

public class CChannel extends Channel {

  protected CChannel(Broker broker, int port) {
    super(broker);
  }

  public CChannel(CircularBuffer in,CircularBuffer out, Broker broker){
    super(broker);
    this.in=in;
    this.out= out;
    disconnected= false;
    
  }

  // added for helping debugging applications.
  public String getRemoteName() {
    throw new RuntimeException("NYI");
  }

  @Override
  public int read(byte[] bytes, int offset, int length) {

    if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 ){
      throw new IllegalArgumentException("the range indicated is illegal");
    }

    if(this.disconnected()){
      throw new IllegalStateException("this channel is already disconnected");
    }

    synchronized(this){

      int readed=0;

      while (readed<length && !disconnected()){
        try {
          bytes[readed+offset]=this.in.pull();
          notifyAll();
          readed++;
        } catch (IllegalStateException e) {
          if(readed!=0){
            return readed;
          }else{
            try {
              //System.out.print("  blockr  ");
              wait();
              //System.out.print("  deblockr  ");
              //continue;
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }
          }
        }
        
      }
      return readed;
    }
  }

  @Override
  public  int write(byte[] bytes, int offset, int length) {

    if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 || length==0 ){
      throw new IllegalArgumentException("the range indicated is illegal");
    }

    if(this.disconnected()){
      throw new IllegalStateException("this channel is already disconnected");
    }
          
    synchronized(remote_end){

      int wrote=0;

      while (wrote<length && !disconnected()){
        try {
          this.out.push(bytes[wrote+offset]);
          remote_end.notifyAll();
          wrote++;
        } catch (IllegalStateException e) {
          if(wrote!=0){
            return wrote;
          }else{
            try {
              remote_end.wait();
              //continue;
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
      return wrote;
    }

  }

  @Override
  public  void disconnect() {

    if(!disconnected()){
      disconnected = true;

      synchronized(this){
        notifyAll();
      }

      synchronized(remote_end){
        remote_end.notifyAll();
        while(!out.empty()){
          try {
            remote_end.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      
      remote_end.disconnect();
    }

  }

  @Override
  public boolean disconnected() {
    return disconnected;
  }

  private boolean disconnected;

  private CircularBuffer in ;

  private CircularBuffer out ;


  private CChannel remote_end;

  public void set_remote_end(CChannel cch){
    this.remote_end=cch;
  }

}



// if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 ){
//       throw new IllegalArgumentException("the range indicated is illegal");
//     }

//     synchronized(remote_end){
//       int wrote=0;
//       while(!disconnected()){
//         switch (wstate) {

//           case wState.WRLENGTH:

//             wrote=0;
          
//             byte[] length_bytes = ByteBuffer.allocate(4).putInt(length).array();
//             while (wrote<4){
//               try {
//                 this.out.push(length_bytes[wrote]);
//               } catch (IllegalStateException e) {
//                 if(wrote!=0){
//                   return -1*wrote;
//                 }else{
//                   try {
//                     wait();
//                     continue;
//                   } catch (InterruptedException e1) {
//                     e1.printStackTrace();
//                   }
//                 }
//               }
//               notify();
//               wrote++;

//             }
//             wstate= wState.WRMESSAGE;
//             //return this.write( bytes, offset, length);
          

//           case wState.WRMESSAGE:

//             wrote=0;
          
//             for (int i=offset;i< length+offset;i++){
//               try {
//                 this.in.push(bytes[i]);
//               } catch (IllegalStateException e) {
//                 if(wrote!=0){
//                   return wrote;
//                 }else{
//                   try {
//                     wait();
//                   } catch (InterruptedException e1) {
//                     e1.printStackTrace();
//                   }
//                 }
//               }
//               notify();
//               wrote++;
//             }
//             wstate= wState.WRLENGTH;
//             return wrote;

//           default:
//             return 0;
//         }
//       }

//       return wrote;
//     }




// if(offset<0 || length<0 || offset >bytes.length || offset+length >bytes.length+1 ){
//       throw new IllegalArgumentException("the range indicated is illegal");
//     }

//     synchronized(this){
//       int result=0;
//       int readed=0;
//       while(!disconnected()){
//         switch (rstate) {
//           case RELENGTH:

//             readed=0;
//             byte[] rd = new byte[4];
//             while (readed<4) {
//               try {
//                 rd[readed]=this.in.pull();
//               } catch (Exception e) {
//                 if(readed!=0){
//                   return -1*readed;
//                 }else{
//                   try {
//                     wait();
//                   } catch (InterruptedException e1) {
//                     e1.printStackTrace();
//                   }
//                 }
//               }
//               notify();
//               readed++;
//             }

//             result = ByteBuffer.wrap(rd).getInt();
//             if (result==0){
//               this.disconnect();
//             }else{
//               rstate=rState.REMESSAGE;
//             }
            
//             break;
          
//           case REMESSAGE:

//             readed=offset;
//             //byte[] rd = new byte[4];
//             while (readed<result+offset) {
//               try {
//                 bytes[readed]=this.in.pull();
//               } catch (Exception e) {
//                 if(readed!=0){
//                   return readed;
//                 }else{
//                   try {
//                     wait();
//                   } catch (InterruptedException e1) {
//                     e1.printStackTrace();
//                   }
//                 }
//               }
//               notify();
//               readed++;
//             }

//             rstate=rState.RELENGTH;
//             return result;
            
          
//           default:
//             break;
//         }
//       }
//     }


//     throw new RuntimeException("NYI");