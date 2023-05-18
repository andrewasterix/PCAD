import java.util.concurrent.LinkedBlockingQueue;
/* Liste Linkate */
public class LL<E> extends LinkedBlockingQueue<E>{
    private boolean finished = false;

    LL() { super();}

    public boolean isFinished() { return finished;}

    public void setFinished(boolean finished) { this.finished = finished;}

}