package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import gnu.trove.a_ref.a;
import gnu.trove.ba.aa;
import gnu.trove.e_ref.ai;
import gnu.trove.map.W_ref;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class THashMap extends aa implements W_ref, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient Object[] _values;

   public THashMap() {
   }

   public THashMap(int var1) {
      super(var1);
   }

   public THashMap(int var1, float var2) {
      super(var1, var2);
   }

   public THashMap(Map var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public THashMap(THashMap var1) {
      this(var1.size());
      this.putAll(var1);
   }

   public int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new Object[var1];
      return var1;
   }

   public Object put(Object var1, Object var2) {
      int var3 = this.insertKey(var1);
      return this.doPut(var2, var3);
   }

   public Object putIfAbsent(Object var1, Object var2) {
      int var3;
      return (var3 = this.insertKey(var1)) < 0 ? this._values[-var3 - 1] : this.doPut(var2, var3);
   }

   private Object doPut(Object var1, int var2) {
      Object var3 = null;
      boolean var4 = true;
      if (var2 < 0) {
         var2 = -var2 - 1;
         var3 = this._values[var2];
         var4 = false;
      }

      this._values[var2] = var1;
      if (var4) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var3;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Map)) {
         return false;
      } else {
         Map var2;
         return (var2 = (Map)var1).size() != this.size() ? false : this.forEachEntry(new THashMap$EqProcedure(this, var2));
      }
   }

   public int hashCode() {
      THashMap$HashProcedure var1 = new THashMap$HashProcedure(this, (THashMap$HashProcedure)null);
      this.forEachEntry(var1);
      return var1.getHashCode();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new THashMap$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   public boolean forEachKey$1d6e2644(a var1) {
      return this.forEach$1d6e2644(var1);
   }

   public boolean forEachValue$1d6e2644(a var1) {
      Object[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var3[var4] == FREE || var3[var4] == REMOVED || var1.i());

      return false;
   }

   public boolean forEachEntry(ai var1) {
      Object[] var2 = this._set;
      Object[] var3 = this._values;
      int var4 = var2.length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] == FREE || var2[var4] == REMOVED || var1.execute(var2[var4], var3[var4]));

      return false;
   }

   public boolean retainEntries(ai var1) {
      boolean var2 = false;
      Object[] var3 = this._set;
      Object[] var4 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var5 = var3.length;

         while(var5-- > 0) {
            if (var3[var5] != FREE && var3[var5] != REMOVED && !var1.execute(var3[var5], var4[var5])) {
               this.removeAt(var5);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   public void transformValues$2467b360(a var1) {
      Object[] var2 = this._values;
      Object[] var3 = this._set;
      int var4 = var2.length;

      while(var4-- > 0) {
         if (var3[var4] != FREE && var3[var4] != REMOVED) {
            var2[var4] = var1.g();
         }
      }

   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      int var3 = this.size();
      Object[] var4 = this._set;
      Object[] var5 = this._values;
      this._set = new Object[var1];
      Arrays.fill(this._set, FREE);
      this._values = new Object[var1];
      var1 = var2;

      while(var1-- > 0) {
         Object var7;
         if ((var7 = var4[var1]) != FREE && var7 != REMOVED) {
            int var6;
            if ((var6 = this.insertKey(var7)) < 0) {
               this.throwObjectContractViolation(this._set[-var6 - 1], var7, this.size(), var3, var4);
            }

            this._values[var6] = var5[var1];
         }
      }

      reportPotentialConcurrentMod(this.size(), var3);
   }

   public Object get(Object var1) {
      int var2;
      return (var2 = this.index(var1)) < 0 ? null : this._values[var2];
   }

   public void clear() {
      if (this.size() != 0) {
         super.clear();
         Arrays.fill(this._set, 0, this._set.length, FREE);
         Arrays.fill(this._values, 0, this._values.length, (Object)null);
      }
   }

   public Object remove(Object var1) {
      Object var2 = null;
      int var3;
      if ((var3 = this.index(var1)) >= 0) {
         var2 = this._values[var3];
         this.removeAt(var3);
      }

      return var2;
   }

   public void removeAt(int var1) {
      this._values[var1] = null;
      super.removeAt(var1);
   }

   public Collection values() {
      return new THashMap$ValueView(this);
   }

   public Set keySet() {
      return new THashMap$KeyView(this);
   }

   public Set entrySet() {
      return new THashMap$EntryView(this);
   }

   public boolean containsValue(Object var1) {
      Object[] var2 = this._set;
      Object[] var3 = this._values;
      int var4;
      if (var1 == null) {
         var4 = var3.length;

         while(var4-- > 0) {
            if (var2[var4] != FREE && var2[var4] != REMOVED && var1 == var3[var4]) {
               return true;
            }
         }

         return false;
      } else {
         var4 = var3.length;

         do {
            do {
               do {
                  if (var4-- <= 0) {
                     return false;
                  }
               } while(var2[var4] == FREE);
            } while(var2[var4] == REMOVED);
         } while(var1 != var3[var4] && !this.equals(var1, var3[var4]));

         return true;
      }
   }

   public boolean containsKey(Object var1) {
      return this.contains(var1);
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put(var3.getKey(), var3.getValue());
      }

   }

   public void writeExternal(ObjectOutput var1) {
   try {
      var1.writeByte(1);
      super.writeExternal(var1);
      var1.writeInt(this._size);
      int var2 = this._set.length;

      while(var2-- > 0) {
         if (this._set[var2] != REMOVED && this._set[var2] != FREE) {
            var1.writeObject(this._set[var2]);
            var1.writeObject(this._values[var2]);
         }
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   public void readExternal(ObjectInput var1) {
   try {
      if (var1.readByte() != 0) {
         super.readExternal(var1);
      }

      int var2 = var1.readInt();
      this.setUp(var2);

      while(var2-- > 0) {
         Object var3 = var1.readObject();
         Object var4 = var1.readObject();
         this.put(var3, var4);
      }

   } catch (IOException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   } catch (ClassNotFoundException ex) {
   Logger.getLogger(TIntObjectHashMap.class.getName()).log(Level.SEVERE, null, ex);
   }
   }

   // $FF: synthetic method
   static boolean access$0(THashMap var0, Object var1, Object var2) {
      return var0.equals(var1, var2);
   }

   // $FF: synthetic method
   static int access$1(THashMap var0, Object var1) {
      return var0.index(var1);
   }
}
