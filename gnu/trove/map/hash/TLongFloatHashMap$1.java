package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TLongFloatProcedure;

class TLongFloatHashMap$1 implements TLongFloatProcedure {
   private boolean first;
   // $FF: synthetic field
   final TLongFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TLongFloatHashMap$1(TLongFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(long var1, float var3) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var3);
      return true;
   }
}
