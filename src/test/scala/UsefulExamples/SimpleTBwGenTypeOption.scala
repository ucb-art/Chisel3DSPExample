package SimpleTB

import chisel3._
import chisel3.util.RegNext
import breeze.math.Complex
import dsptools.numbers.{Real, DspReal, DspComplex}
import dsptools.numbers.implicits._
import dsptools.{VerboseDspTester, VerboseDspTesterOptionsManager, VerboseDspTesterOptions}
import org.scalatest.{FlatSpec, Matchers}
import chisel3.iotesters.TesterOptions

case class TestParams(
    val smallW: Int = 4,
    val bigW: Int = 8,
    val biggerW: Int = 16,
    val smallBP: Int = 4,
    val bigBP: Int = 8,
    val vecLen: Int = 4,
    val posLit: Double = 3.3,
    val negLit: Double = -3.3,
    val lutVals: Seq[Double] = Seq(-3.3, -2.2, -1.1, 0, 1.1, 2.2, 3.3),
    val bp: Int = 4) {

  val genLongF = FixedPoint(biggerW.W, bigBP.BP)
  val genShortF = FixedPoint(bigW.W, smallBP.BP)
  val genLongS = SInt(bigW.W)
  val genShortS = SInt(smallW.W)
  val genR = DspReal()

}

class Interface[R <: Data:Real](genShort: R, genLong: R, includeR: Boolean, p: TestParams) extends Bundle {

  val smallW = p.smallW.W
  val bigW = p.bigW.W
  val biggerW = p.biggerW.W
  val smallBP = p.smallBP.BP
  val bigBP = p.bigBP.BP
  val vecLen = p.vecLen

  val genS = genShort.chiselCloneType
  val genL = genLong.chiselCloneType
  val r = if (includeR) Some(DspReal()) else None
  val sS = SInt(smallW)
  val sL = SInt(bigW)
  val fS = FixedPoint(bigW, smallBP)
  val fL = FixedPoint(biggerW, bigBP)
  val uS = UInt(smallW)
  val uL = UInt(bigW)
  val b = Bool()
  val cGenL = DspComplex(genLong, genLong)
  val cFS = DspComplex(FixedPoint(bigW, smallBP), FixedPoint(bigW, smallBP))
  val cR = DspComplex(DspReal(), DspReal())
  val bu = new Bundle {
    val s = SInt(smallW)
    val f = FixedPoint(bigW, smallBP)
    val u = UInt(smallW)
    val g = genShort.chiselCloneType
  }
  val vU = Vec(vecLen, UInt(smallW))
  val vS = Vec(vecLen, SInt(smallW))  
  
  override def cloneType: this.type = new Interface(genShort, genLong, includeR, p).asInstanceOf[this.type]
}

class SimpleModule[R <: Data:Real](genShort: R, genLong: R, val includeR: Boolean, p: TestParams) extends Module {

  val posLit = p.posLit
  val negLit = p.negLit
  val lutVals = p.lutVals
  val bp = p.bp


  val io = IO(new Bundle {
    val i = Input(new Interface(genShort, genLong, includeR, p))
    val o = Output(new Interface(genShort, genLong, includeR, p)) })  

  val litRP = if (includeR) Some(DspReal(posLit)) else None
  val litRN = if (includeR) Some(DspReal(negLit)) else None

  val pos = new Bundle {
    val litG = genShort.double2T(posLit)
    val litS = posLit.round.toInt.S
    val litF = FixedPoint.fromDouble(posLit, binaryPoint = bp)
  }

  val neg = new Bundle {
    val litG = genShort.double2T(negLit)
    val litS = negLit.round.toInt.S
    val litF = FixedPoint.fromDouble(negLit, binaryPoint = bp)
  }

  val litB = Bool(true)
  val litU = posLit.round.toInt.U
  val litC = DspComplex(FixedPoint.fromDouble(posLit, binaryPoint = bp), FixedPoint.fromDouble(negLit, binaryPoint = bp))
  val lutGen = Vec(lutVals map {x => genShort.double2TFixedWidth(x)})
  val lutS = Vec(lutVals map (_.round.toInt.S))

}

class LitTester[R <: Data:Real](c: SimpleModule[R]) extends VerboseDspTester(c) {
  
  val posLit = c.posLit
  val negLit = c.negLit
  val lutVals = c.lutVals

  if (c.includeR) {
    dspPeek(c.litRP.get)
    dspExpect(c.litRP.get, posLit)
    dspPeek(c.litRN.get)
    dspExpect(c.litRN.get, negLit)
  }
  
  c.pos.elements foreach { case (s, d) => {
    dspPeek(d)
    dspExpect(d, posLit)
  } }

  c.neg.elements foreach { case (s, d) => {
    dspPeek(d)
    dspExpect(d, negLit)
  } }

  peek(c.litB)
  expect(c.litB, true)
  peek(c.litU)
  expect(c.litU, posLit.toInt)

  dspPeek(c.litC)
  dspExpect(c.litC, Complex(posLit, negLit))

  peek(c.lutS)
  c.lutS.zipWithIndex foreach { case (x,i) => {
    expect(x, lutVals(i).round.toInt) } }

  peek(c.lutGen)
  c.lutGen.zipWithIndex foreach { case (x, i) => {
    dspExpect(x, lutVals(i))
  } }
}

class SimpleTBSpec extends FlatSpec with Matchers {

  val p = TestParams()
  val testerOptionsGlobal = TesterOptions(
      isVerbose = false,
      backendName = "verilator",
      isGenVerilog = true)


  behavior of "simple module io + lits"

  it should "read lits with gen = sint properly" in {
    val options = new VerboseDspTesterOptionsManager {
      verboseDspTesterOptions = VerboseDspTesterOptions(
          genVerilogTb = false,
          isVerbose = true)
      testerOptions = testerOptionsGlobal
    }
    val includeR = true
    dsptools.Driver.execute(() => new SimpleModule(p.genShortS, p.genLongS, includeR, p), options) { c =>
      new LitTester(c)
    } should be (true)

  }

}