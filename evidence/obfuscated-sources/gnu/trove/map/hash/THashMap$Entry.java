package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import java.util.ConcurrentModificationException;
import java.util.Map.Entry;

final class THashMap$Entry implements Entry {
   private Object key;
   private Object val;
   private final int index;
   // $FF: synthetic field
   final THashMap this$0;

   THashMap$Entry(THashMap var1, Object var2, Object var3, int var4) {
      this.this$0 = var1;
      this.key = var2;
      this.val = var3;
      this.index = var4;
   }

   public final Object getKey() {
      return this.key;
   }

   public final Object getValue() {
      return this.val;
   }

   public final Object setValue(Object var1) {
      if (this.this$0._values[this.index] != this.val) {
         throw new ConcurrentModificationException();
      } else {
         Object var2 = this.val;
         this.this$0._values[this.index] = var1;
         this.val = var1;
         return var2;
      }
   }

   public final boolean equals(Object var1) {
      if (var1 instanceof Entry) {
         Entry var3 = (Entry)var1;
         return THashMap.access$0(this.this$0, this.getKey(), var3.getKey()) && THashMap.access$0(this.this$0, this.getValue(), this.getValue());
      } else {
         return false;
      }
   }

   public final int hashCode() {
      return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
   }

   public final String toString() {
      return this.key + "=" + this.val;
   }
}
