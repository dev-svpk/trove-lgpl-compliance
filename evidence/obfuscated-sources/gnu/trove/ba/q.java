package gnu.trove.ba;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class q extends aa {
   static final long serialVersionUID = 8766048185963756400L;
   protected gnu.trove.g_ref.a strategy;

   public q() {
   }

   public q(gnu.trove.g_ref.a var1) {
      this.strategy = var1;
   }

   public q(gnu.trove.g_ref.a var1, int var2) {
      super(var2);
      this.strategy = var1;
   }

   public q(gnu.trove.g_ref.a var1, int var2, float var3) {
      super(var2, var3);
      this.strategy = var1;
   }

   protected int hash(Object var1) {
      return this.strategy.a();
   }

   protected boolean equals(Object var1, Object var2) {
      return var2 != REMOVED && this.strategy.b();
   }

   public void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           super.writeExternal(var1);
           var1.writeObject(this.strategy);
       } catch (IOException ex) {
           Logger.getLogger(q.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

   public void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           super.readExternal(var1);
           this.strategy = (gnu.trove.g_ref.a)var1.readObject();
       } catch (IOException ex) {
           Logger.getLogger(q.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ClassNotFoundException ex) {
           Logger.getLogger(q.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
