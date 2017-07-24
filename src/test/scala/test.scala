// pure width with input
// pure range with input
// range + width knowing output range
// simulate + plug in
// firrtl interpreter -- location + max size (all nodes, some of nodes)
// permanent file -- feed to firrtl right before emission
// Operate on low firrtl? 
// can you convert to uint if interval lower bound is 0?
// easy way to probe width -- print out all widths in module; don't need to descend?
// easy switch from range to width (uint, sint) for characterization
// as uint is not a way to reduce bits
// tester need to support interval

// Unknown range doesn't work?
// can i merge march changes?
// corner cases
// show that across module boundaries, this works???

// check ranges in firrtl outputs

// verilator backend (dsptester) doesn't work with uninferred output width
// How to cast UInt to Interval

/*
in range -> chisel -> firrtl -> lo firrtl -> firttl interpreter -> low firrtl -> firrtl interpreter: input to interp: list of nodes to check (all or none) firrtl intp output: node/range (list of saved files)
xample where this is useful is x_n - x_(n-1)
*/

// 7^3 vs. 4^3 8 vs 12? (why is it 12 and not 9???)

// multiply, mux

package Experiment

import chisel3._
import chisel3.experimental.FixedPoint
import dsptools.{DspTester, DspTesterOptionsManager, DspTesterOptions}
import org.scalatest.{FlatSpec, Matchers}

import dsptools.numbers.{RealBits}
import dsptools.numbers.implicits._
import dsptools.DspContext
import iotesters.TesterOptions

import iotesters.TesterOptionsManager
import iotesters._

import chisel3.experimental.{ChiselRange, Interval}
import chisel3.internal.firrtl.KnownIntervalRange
import chisel3.internal.firrtl._

class UIntervalIO extends Bundle {
  val b = Input(Bool())
  val in1 = Input(Interval(range"[0,4]"))
  val in2 = Input(Interval(range"[0,3]"))
  val in3 = Input(Interval(range"[0,4]"))
  val out1 = Output(Interval(UnknownRange))
  val out2 = Output(Interval(UnknownRange))
  val out3 = Output(Interval(UnknownRange))
}





/*

object IntervalConversion {
  // TODO: type safe?
  def createUIntIO(intervalIO: Record): Record = {
    val newElements = intervalIO.elements.map { case (name, elem) =>
      elem match {
        case int: Interval =>
          val range = int.range
          val elemBits = int.dir match {
            case chisel3.core.Direction.Input =>
              UInt(range).asInput
            case chisel3.core.Direction.Output =>
              UInt(range).asOutput
            case _ =>
              throw new Exception("Invalid direction :(") 
          }
          (name, elemBits)
        case _ => 
          // TODO: Expand aggregates
          (name, elem)
      }
    }
    new CustomBundle(newElements: _*)
  }
  def createConnectUIntIO(intervalIO: Record): Record = {
    val uintIO = createUIntIO(intervalIO)
    uintIO.elements foreach { case (name, elem) =>
      elem.
    }





  }
}

*/








class SimpleDspIo[T <: Data:RealBits](gen: T) extends Bundle {
  val b = Input(Bool())

  val in1 = Input(Interval(range"[0,4]")) //Input(Interval(6.W, 3.BP, range"[0,4]"))
  val in2 = Input(Interval(range"[0,3]")) //Input(Interval(6.W, 3.BP, range"[0,4]"))
  val in3 = Input(UInt(range"[0,4]"))
  val out1= Output(UInt(Width()))
  val out2 = Output(SInt(Width()))
  val out3 = Output(UInt(Width()))
  val out4 = Output(UInt(Width()))
  
  override def cloneType: this.type = new SimpleDspIo(gen).asInstanceOf[this.type]
}

class SimpleDspModule[T <: Data:RealBits](gen: T, val addPipes: Int) extends Module {

 // val intervalIO = new UIntervalIO
 // val iox = IntervalConversion.createBitIO(intervalIO)


// auto wrap in io? pass in which to convert to io!!! (original or new)

  // create new module 
  // input/output all uint (out is unknown)
  // internal input/output all interval (out is unknown)

  val io = IO(new SimpleDspIo(gen))




  io.elements.foreach { case (name, elem) => 
    println(name)
    elem match {
      case x: Interval => 
        println(x.range)
      case _ =>
    }

  }

  //val in1 = Wire(Interval(range"[0,4]"))
  //val in2 = Wire(Interval(range"[0,4]"))

  //in1 := 2.I
  //in2 := 2.I

  val a = 2.I
  val b = -6.I
  val c = 9.I

  val r = Mux(io.b, a, c)

  val result = io.in1 +& io.in2

  io.out1 := result.asUInt
  io.out2 := r.asSInt
  io.out3 := (io.in1 * io.in1 * io.in1).asUInt
  io.out4 := (io.in3 * io.in3 * io.in3).asUInt
}

class SimpleDspModuleTester[T <: Data:RealBits](c: SimpleDspModule[T]) extends PeekPokeTester(c) {
  poke(c.io.b, false)
  peek(c.io.out2)
}

class SimpleDspModuleSpec extends FlatSpec with Matchers {
  val testOptions = new DspTesterOptionsManager {
    dspTesterOptions = DspTesterOptions(
        fixTolLSBs = 1,
        genVerilogTb = true,
        isVerbose = true)
    testerOptions = TesterOptions(
        isVerbose = true,
        backendName = "firrtl")
    commonOptions = commonOptions.copy(targetDirName = "test_run_dir/intervals")
  }

  // val testOptions = new DspTesterOptionsManager

  behavior of "simple dsp module"

  it should "properly add fixed point types" in {
    /*dsptools.Driver.execute(() => new SimpleDspModule(FixedPoint(16.W, 12.BP), addPipes = 3), testOptions) { c =>
      new SimpleDspModuleTester(c)
    } should be (true)*/

    iotesters.Driver.execute(() => new SimpleDspModule(FixedPoint(16.W, 12.BP), addPipes = 3), testOptions) { c =>
      new SimpleDspModuleTester(c)
    } should be (true)
  }

}