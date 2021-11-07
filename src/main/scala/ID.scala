package FiveStage
import chisel3._
import chisel3.util.{ BitPat, MuxCase }
import chisel3.experimental.MultiIOModule


class InstructionDecode extends MultiIOModule {



    val io = IO(new Bundle {
        val ins=Input(new Instruction)
        val op_0=Output(UInt(32.W))
        val op_1=Output(UInt(32.W))
        
        val alu_op=Output(UInt(4.W))
        /**
        * TODO: Your code here.
        */
        
        val dst=Output(UInt(5.W))
        
        val waddr=Input(UInt(32.W))
        val wdata=Input(UInt(32.W))
    })

    val registers = Module(new Registers)
    val decoder   = Module(new Decoder).io
    
    val ins=io.INS
    
    io.dst:=ins.registerRd
    
    val simm=Wire(SInt(32.W))
    simm:=ins.immediateIType
    
    
    io.op_0:=simm.asUInt
    io.op_1:=registers.io.readData1
  
    /**
    * Setup. You should not change this code
    */
    registers.testHarness.setup := testHarness.registerSetup
    
    
    /**
    * TODO: Your code here.
    */
    registers.io.readAddress1 := ins.registerRs1 
    registers.io.readAddress2 := 0.U
    registers.io.writeEnable  := true.B
    registers.io.writeAddress := io.waddr 
    registers.io.writeData    := io.wdata 
    
    io.alu_op:=ALUOps.ADD
    
    decoder.instruction := 0.U.asTypeOf(new Instruction)
    
    // Don't touch the test harness
    val testHarness = IO(
        new Bundle {
            val registerSetup = Input(new RegisterSetupSignals)
            val registerPeek  = Output(UInt(32.W))
            val testUpdates   = Output(new RegisterUpdates)
    })
    testHarness.registerPeek    := registers.io.readData1
    testHarness.testUpdates     := registers.testHarness.testUpdates
}



