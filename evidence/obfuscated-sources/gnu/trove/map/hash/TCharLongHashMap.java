package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.f;
import gnu.trove.a_ref.a;
import gnu.trove.ba.o;
import gnu.trove.c_ref.q;
import gnu.trove.e_ref.aa;
import gnu.trove.f_ref.b;
import gnu.trove.map.n;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCharLongHashMap extends o implements n, Externalizable {
   static final long serialVersionUID = 1L;
   protected transient long[] _values;

   public TCharLongHashMap() {
   }

   public TCharLongHashMap(int var1) {
      super(var1);
   }

   public TCharLongHashMap(int var1, float var2) {
      super(var1, var2);
   }

   public TCharLongHashMap(int var1, float var2, char var3, long var4) {
      super(var1, var2, var3, var4);
   }

   public TCharLongHashMap(char[] var1, long[] var2) {
      super(Math.max(var1.length, var2.length));
      int var3 = Math.min(var1.length, var2.length);

      for(int var4 = 0; var4 < var3; ++var4) {
         this.put(var1[var4], var2[var4]);
      }

   }

   public TCharLongHashMap(n var1) {
      super(var1.size());
      if (var1 instanceof TCharLongHashMap) {
         TCharLongHashMap var2 = (TCharLongHashMap)var1;
         this._loadFactor = var2._loadFactor;
         this.no_entry_key = var2.no_entry_key;
         this.no_entry_value = var2.no_entry_value;
         if (this.no_entry_key != 0) {
            Arrays.fill(this._set, this.no_entry_key);
         }

         if (this.no_entry_value != 0L) {
            Arrays.fill(this._values, this.no_entry_value);
         }

         this.setUp((int)Math.ceil((double)(10.0F / this._loadFactor)));
      }

      this.putAll(var1);
   }

   protected int setUp(int var1) {
      var1 = super.setUp(var1);
      this._values = new long[var1];
      return var1;
   }

   protected void rehash(int var1) {
      int var2 = this._set.length;
      char[] var3 = this._set;
      long[] var4 = this._values;
      byte[] var5 = this._states;
      this._set = new char[var1];
      this._values = new long[var1];
      this._states = new byte[var1];
      var1 = var2;

      while(var1-- > 0) {
         if (var5[var1] == 1) {
            char var6 = var3[var1];
            var2 = this.insertKey(var6);
            this._values[var2] = var4[var1];
         }
      }

   }

   public long put(char var1, long var2) {
      int var4 = this.insertKey(var1);
      return this.doPut(var1, var2, var4);
   }

   public long putIfAbsent(char var1, long var2) {
      int var4;
      return (var4 = this.insertKey(var1)) < 0 ? this._values[-var4 - 1] : this.doPut(var1, var2, var4);
   }

   private long doPut(char var1, long var2, int var4) {
      long var5 = this.no_entry_value;
      boolean var7 = true;
      if (var4 < 0) {
         var4 = -var4 - 1;
         var5 = this._values[var4];
         var7 = false;
      }

      this._values[var4] = var2;
      if (var7) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var5;
   }

   public void putAll(Map var1) {
      this.ensureCapacity(var1.size());
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.put((Character)var3.getKey(), (Long)var3.getValue());
      }

   }

   public void putAll(n var1) {
      this.ensureCapacity(var1.size());
      q var2 = var1.iterator();

      while(var2.hasNext()) {
         var2.advance();
         this.put(var2.key(), var2.value());
      }

   }

   public long get(char var1) {
      int var2;
      return (var2 = this.index(var1)) < 0 ? this.no_entry_value : this._values[var2];
   }

   public void clear() {
      super.clear();
      Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
      Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
      Arrays.fill(this._states, 0, this._states.length, (byte)0);
   }

   public boolean isEmpty() {
      return this._size == 0;
   }

   public long remove(char var1) {
      long var2 = this.no_entry_value;
      int var4;
      if ((var4 = this.index(var1)) >= 0) {
         var2 = this._values[var4];
         this.removeAt(var4);
      }

      return var2;
   }

   protected void removeAt(int var1) {
      this._values[var1] = this.no_entry_value;
      super.removeAt(var1);
   }

   public b keySet() {
      return new TCharLongHashMap$TKeyView(this);
   }

   public char[] keys() {
      char[] var1 = new char[this.size()];
      char[] var2 = this._set;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public char[] keys(char[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new char[var2];
      }

      char[] var6 = this._set;
      byte[] var3 = this._states;
      int var4 = var6.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var6[var4];
         }
      }

      return var1;
   }

   public f valueCollection() {
      return new TCharLongHashMap$TValueView(this);
   }

   public long[] values() {
      long[] var1 = new long[this.size()];
      long[] var2 = this._values;
      byte[] var3 = this._states;
      int var4 = var2.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var2[var4];
         }
      }

      return var1;
   }

   public long[] values(long[] var1) {
      int var2 = this.size();
      if (var1.length < var2) {
         var1 = new long[var2];
      }

      long[] var6 = this._values;
      byte[] var3 = this._states;
      int var4 = var6.length;
      int var5 = 0;

      while(var4-- > 0) {
         if (var3[var4] == 1) {
            var1[var5++] = var6[var4];
         }
      }

      return var1;
   }

   public boolean containsValue(long var1) {
      byte[] var3 = this._states;
      long[] var4;
      int var5 = (var4 = this._values).length;

      do {
         if (var5-- <= 0) {
            return false;
         }
      } while(var3[var5] != 1 || var1 != var4[var5]);

      return true;
   }

   public boolean containsKey(char var1) {
      return this.contains(var1);
   }

   public q iterator() {
      return new TCharLongHashMap$TCharLongHashIterator(this, this);
   }

   public boolean forEachKey(gnu.trove.e_ref.q var1) {
      return this.forEach(var1);
   }

   public boolean forEachValue(aa var1) {
      byte[] var2 = this._states;
      long[] var3;
      int var4 = (var3 = this._values).length;

      do {
         if (var4-- <= 0) {
            return true;
         }
      } while(var2[var4] != 1 || var1.execute(var3[var4]));

      return false;
   }

   public boolean forEachEntry(gnu.trove.e_ref.o var1) {
      byte[] var2 = this._states;
      char[] var3 = this._set;
      long[] var4 = this._values;
      int var5 = var3.length;

      do {
         if (var5-- <= 0) {
            return true;
         }
      } while(var2[var5] != 1 || var1.execute(var3[var5], var4[var5]));

      return false;
   }

   public void transformValues$6f4d57c3(a var1) {
      byte[] var2 = this._states;
      long[] var3;
      int var4 = (var3 = this._values).length;

      while(var4-- > 0) {
         if (var2[var4] == 1) {
            var3[var4] = var1.f();
         }
      }

   }

   public boolean retainEntries(gnu.trove.e_ref.o var1) {
      boolean var2 = false;
      byte[] var3 = this._states;
      char[] var4 = this._set;
      long[] var5 = this._values;
      this.tempDisableAutoCompaction();

      try {
         int var6 = var4.length;

         while(var6-- > 0) {
            if (var3[var6] == 1 && !var1.execute(var4[var6], var5[var6])) {
               this.removeAt(var6);
               var2 = true;
            }
         }
      } finally {
         this.reenableAutoCompaction(true);
      }

      return var2;
   }

   public boolean increment(char var1) {
      return this.adjustValue(var1, 1L);
   }

   public boolean adjustValue(char var1, long var2) {
      int var4;
      if ((var4 = this.index(var1)) < 0) {
         return false;
      } else {
         long[] var10000 = this._values;
         var10000[var4] += var2;
         return true;
      }
   }

   public long adjustOrPutValue(char var1, long var2, long var4) {
      long var8;
      int var10;
      boolean var11;
      if ((var10 = this.insertKey(var1)) < 0) {
         var10 = -var10 - 1;
         long[] var10000 = this._values;
         var8 = var10000[var10] += var2;
         var11 = false;
      } else {
         var8 = this._values[var10] = var4;
         var11 = true;
      }

      if (var11) {
         this.postInsertHook(this.consumeFreeSlot);
      }

      return var8;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof n)) {
         return false;
      } else {
         n var15;
         if ((var15 = (n)var1).size() != this.size()) {
            return false;
         } else {
            long[] var2 = this._values;
            byte[] var3 = this._states;
            long var5 = this.getNoEntryValue();
            long var7 = var15.getNoEntryValue();
            int var4 = var2.length;

            while(var4-- > 0) {
               if (var3[var4] == 1) {
                  char var9 = this._set[var4];
                  long var11 = var15.get(var9);
                  long var13;
                  if ((var13 = var2[var4]) != var11 && var13 != var5 && var11 != var7) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int var1 = 0;
      byte[] var2 = this._states;
      int var3 = this._values.length;

      while(var3-- > 0) {
         if (var2[var3] == 1) {
            var1 += gnu.trove.b_ref.b.a(this._set[var3]) ^ gnu.trove.b_ref.b.a(this._values[var3]);
         }
      }

      return var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      this.forEachEntry(new TCharLongHashMap$1(this, var1));
      var1.append("}");
      return var1.toString();
   }

   public void writeExternal(ObjectOutput var1) {
       try {
           var1.writeByte(0);
           super.writeExternal(var1);
           var1.writeInt(this._size);
           int var2 = this._states.length;
           
           while(var2-- > 0) {
               if (this._states[var2] == 1) {
                   var1.writeChar(this._set[var2]);
                   var1.writeLong(this._values[var2]);
               }
           }} catch (IOException ex) {
           Logger.getLogger(TCharLongHashMap.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   public void readExternal(ObjectInput var1) {
       try {
           var1.readByte();
           super.readExternal(var1);
           int var2 = var1.readInt();
           this.setUp(var2);
           
           while(var2-- > 0) {
               char var3 = var1.readChar();
               long var4 = var1.readLong();
               this.put(var3, var4);
           }} catch (IOException ex) {
           Logger.getLogger(TCharLongHashMap.class.getName()).log(Level.SEVERE, null, ex);
       }

   }

   // $FF: synthetic method
   static char access$0(TCharLongHashMap var0) {
      return var0.no_entry_key;
   }

   // $FF: synthetic method
   static int access$1(TCharLongHashMap var0) {
      return var0._size;
   }

   // $FF: synthetic method
   static long access$2(TCharLongHashMap var0) {
      return var0.no_entry_value;
   }
}
