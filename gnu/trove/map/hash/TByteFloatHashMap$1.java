package gnu.trove.map.hash;

import gnu.trove.iterator.TIterator;
import gnu.trove.procedure.TByteFloatProcedure;

class TByteFloatHashMap$1 implements TByteFloatProcedure {
   private boolean first;
   // $FF: synthetic field
   final TByteFloatHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TByteFloatHashMap$1(TByteFloatHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(byte var1, float var2) {
      if (this.first) {
         this.first = false;
      } else {
         this.val$buf.append(", ");
      }

      this.val$buf.append(var1);
      this.val$buf.append("=");
      this.val$buf.append(var2);
      return true;
   }
}
