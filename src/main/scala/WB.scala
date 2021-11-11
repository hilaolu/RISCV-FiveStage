package FiveStage
import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class WriteBack extends MultiIOModule {
        
    val io = IO(
        new Bundle {
            val in=Input(new WBRegs.Contents)            
            val out=Output(new WBRegs.Contents)            
            val mem_data=Input(UInt(32.W))
    })
    
    io.out:=io.in    
    
    io.out.reg_data:=Mux(io.in.mem_op,io.mem_data,io.in.reg_data)
    io.out.mem_op:=DontCare
    
    

}
