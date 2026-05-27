package gnu.trove.impl.hash;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class TCustomObjectHash extends TObjectHash {
   static final long serialVersionUID = 8766048185963756400L;
   protected gnu.trove.strategy.HashingStrategy strategy;

   public TCustomObjectHash() {
   }

   public TCustomObjectHash(gnu.trove.strategy.HashingStrategy var1) {
      this.strategy = var1;
   }

   public TCustomObjectHash(gnu.trove.strategy.HashingStrategy var1, int var2) {
      super(var2);
      this.strategy = var1;
   }

   public TCustomObjectHash(gnu.trove.strategy.HashingStrategy var1, int var2, float var3) {
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
           Logger.getLogger(TCustomObjectHash.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

   public void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           super.readExternal(var1);
           this.strategy = (gnu.trove.strategy.HashingStrategy)var1.readObject();
       } catch (IOException ex) {
           Logger.getLogger(TCustomObjectHash.class.getName()).log(Level.SEVERE, null, ex);
       } catch (ClassNotFoundException ex) {
           Logger.getLogger(TCustomObjectHash.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
}
