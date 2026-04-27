package gnu.trove.map.hash;

import gnu.trove.c_ref.U_ref;
import gnu.trove.e_ref.i;

class TByteShortHashMap$1 implements i {
   private boolean first;
   // $FF: synthetic field
   final TByteShortHashMap this$0;
   // $FF: synthetic field
   private final StringBuilder val$buf;

   TByteShortHashMap$1(TByteShortHashMap var1, StringBuilder var2) {
      this.this$0 = var1;
      this.val$buf = var2;
      this.first = true;
   }

   public boolean execute(byte var1, short var2) {
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
