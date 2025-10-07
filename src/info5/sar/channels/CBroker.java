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
import java.util.HashMap;
import java.util.Map;

import info5.sar.utils.Rdv;


public class CBroker extends Broker {

  public CBroker(String name) {
    super(name);
    L = new HashMap<>();
    try {
      CBrokerManager.post(this);
      
    } catch (Exception e) {
      System.out.print("this name already exits");
    }
  }

  @Override
  public  Channel accept(int port)  {
    Rdv rdv = new Rdv(this);
    synchronized(L){
      if(L.get(port)!=null){
        throw new IllegalArgumentException("A task is already accepting on this port number");
      }
      L.put(port, rdv);
      L.notifyAll();
    }
    return rdv.accept_meet();
    
    
  }

  @Override
  public Channel connect(String name, int port) {

    CBroker broker;
    broker= CBrokerManager.get(name);
    
    try {
      return broker.connect(port);
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    }

  }
  
  private Channel connect(int port) throws InterruptedException {

    synchronized(L){
      while (L.get(port)==null) {
        L.wait();
      }
    }
    Channel ch= L.get(port).connect_meet();
    synchronized(L){
      L.put(port, null);
    }
    return ch;
    
  }

  private Map<Integer, Rdv> L ;


}
