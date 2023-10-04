package bandeau;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Classe utilitaire pour représenter la classe-association UML
 */
class ScenarioElement {

    Effect effect;
    int repeats;

    ScenarioElement(Effect e, int r) {
        effect = e;
        repeats = r;
    }
}
/**
 * Un scenario mémorise une liste d'effets, et le nombre de repetitions pour chaque effet
 * Un scenario sait se jouer sur un bandeau.
 */
public class Scenario extends Thread{

    private final List<ScenarioElement> myElements = new LinkedList<>();

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * Ajouter un effect au scenario.
     *
     * @param e l'effet à ajouter
     * @param repeats le nombre de répétitions pour cet effet
     */
    public void addEffect(Effect e, int repeats) {

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            //si il y a une erreur on interrompt le thread
            Thread.currentThread().interrupt();
        }
        // on lock l'écriture pour éviter les conflits
        readWriteLock.writeLock().lock();
        // on ajoute l'effet et le nombre de répétitions
        myElements.add(new ScenarioElement(e, repeats));
        // on délock l'écriture
        readWriteLock.writeLock().unlock();
    }

    /**
     * Jouer ce scenario sur un bandeau
     *
     * @param b le bandeau ou s'afficher.
     */
    public void playOn(SousClasseBandeau b) {
        Thread t1 = new Thread(() -> {
        // on lock la lecture pour éviter les conflits
            readWriteLock.readLock().lock();
            // on lock le bandeau pour qu'il ne puisse pas recevoir plusieurs scénarios en même temps
            b.lock();
        for (ScenarioElement element : myElements) {
            for (int repeats = 0; repeats < element.repeats; repeats++) {
                element.effect.playOn(b);
            }
        }
        //quand le scénario est fini on délock le bandeau et la lecture
            b.unlock();
            readWriteLock.readLock().unlock();
        });
        // on lance le thread
        t1.start();
    }
}
