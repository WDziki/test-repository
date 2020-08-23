
package d_zipper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Main extends JFrame{

    public static void main(String[] args) {

        new Main().setVisible(true);
    }
    
    public Main() {
        
    this.setBounds(1000, 0, 500, 500);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    Action akcjaDodawania = new Akcja(nazwaBdodaj/*"dodaj"*/, "dodaje pliki", "ctrl D");
    Action akcjaUsuwania = new Akcja(nazwaBusun, "usuwa pliki", "ctrl U");
    Action akcjaZipowania = new Akcja(nazwaBzip, "pakuje pliki", "ctrl Z");
    
    this.setJMenuBar(pasekMenu);
    JMenu menuPlik = pasekMenu.add(new JMenu("plik"));
    JMenuItem menuDodaj = menuPlik.add(akcjaDodawania);
    JMenuItem menuUsun = menuPlik.add(akcjaUsuwania);
    JMenuItem menuZip = menuPlik.add(akcjaZipowania);
    
    bDodaj = new JButton(akcjaDodawania);
    bUsun = new JButton(akcjaUsuwania);
    bZip = new JButton(akcjaZipowania);
   
    GroupLayout layout = new GroupLayout (this.getContentPane());
    this.getContentPane().setLayout(layout);
    
    layout.setAutoCreateContainerGaps(true);
    layout.setAutoCreateGaps(true);
    layout.setHorizontalGroup(layout.createSequentialGroup()
            //.addComponent(lista, 100, 150, Short.MAX_VALUE)//rozszerzanie w boki
            .addComponent(scroll, 100, 150, Short.MAX_VALUE)
            .addContainerGap(10, 10)// gapka przyciskow od listy
            .addGroup(layout.createParallelGroup()
                            .addComponent(bDodaj)
                            .addComponent(bUsun)
                            .addComponent(bZip)
                    )
        );
    layout.setVerticalGroup(layout.createParallelGroup()
            //.addComponent(lista, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)//rozmiar w pionie
            .addComponent(scroll, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)//podmieniona lista za scroll
            .addGroup(layout.createSequentialGroup()
                            .addComponent(bDodaj)
                            .addComponent(bUsun)
                            .addGap(10, 10, Short.MAX_VALUE)
                            .addComponent(bZip)
                    )
    );
    
    lista.setBorder(BorderFactory.createEtchedBorder());
    this.pack();
    }


    String nazwaBdodaj = "Dodaj";
    String nazwaBusun = "Usun";
    String nazwaBzip = "Zip";
    private JButton bDodaj;
    private JButton bUsun;
    private JButton bZip;
    private JMenuBar pasekMenu = new JMenuBar();
    private DefaultListModel modelListy = new DefaultListModel() {//rozszerzamy klase anonimowo 
        ArrayList listaSciezek = new ArrayList();
        
        @Override
        public void addElement(Object obj) {
            listaSciezek.add(obj);//dodaje sciezki obiektow do ArrayListy (C:\Users\...d_Zipper\build.xml, ...manifest.mf itd ]
            super.addElement(((File)obj).getName());// teraz w lini 130 dodajemy nazwe do modelListy(a nie sciezke)
        } 
        @Override //wiec musimy nadpisac metode get, zeby w lini 136(get(i)) miec sciezki a nie tylko nazwy(zeby porownac) 
        public Object get(int index) {
            return listaSciezek.get(index);
        }
        @Override
        public Object remove(int index) {
           listaSciezek.remove(index);//dodoatkow usuwamy z listy
           return super.remove(index);//robi to co zywlke
        }
    };
    private JList lista = new JList(modelListy);
    private JFileChooser wybieracz = new JFileChooser();
    JScrollPane scroll = new JScrollPane(lista);

    
    private class Akcja extends AbstractAction {

        public Akcja (String nazwa, String opis, String skrot) {
            this.putValue(Action.NAME, nazwa);
            this.putValue(Action.SHORT_DESCRIPTION, opis);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(skrot));
        }
        public Akcja (String nazwa, String opis, String skrot, Icon ikona) {
            this(nazwa, opis, skrot);
            this.putValue(Action.SMALL_ICON, ikona);
        }
        @Override
        public boolean accept(Object sender) {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println(e.getActionCommand());//zwraca komende z 
            if (e.getActionCommand().equals(nazwaBdodaj))
                dodajWpisyDoArchiwum();
            if (e.getActionCommand().equals(nazwaBusun))
                usuwanieWpisow();
            if (e.getActionCommand().equals(nazwaBzip))
                stworzArchiwumZip();
        }
        private void dodajWpisyDoArchiwum () {
            wybieracz.setCurrentDirectory(new File(System.getProperty("user.dir")));
            wybieracz.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            wybieracz.setMultiSelectionEnabled(true);
            
            int tmp = wybieracz.showDialog(rootPane, "dodaj do archiwum");
            if (tmp == JFileChooser.APPROVE_OPTION) {
                File [] sciezki = wybieracz.getSelectedFiles();
                for (File sciezka: sciezki)
                    if (czySiePowtarza(sciezka.getPath()) == false)
                        modelListy.addElement(sciezka);
            }
        }
        private boolean czySiePowtarza (String testowanyWpis) {
            for (int i = 0; i < modelListy.getSize(); i++)
                if (((File)modelListy.get(i)).getPath().equals(testowanyWpis))
                    return true;
        return false;
        }
        private void usuwanieWpisow() {
            int[] tmp = lista.getSelectedIndices();
            
            for (int i = 0; i < tmp.length; i++) {
//                System.out.println(tmp[i]);
//                System.out.println(i);
                modelListy.remove(tmp[i]-i);// - i bo cofa wielkos listy jak usunie jakis element
            }
        }
        private void stworzArchiwumZip () {
            wybieracz.setCurrentDirectory(new File(System.getProperty("user.dir")));
//            if (wybieracz.getSelectedFile() == null)
                wybieracz.setSelectedFile(new File(System.getProperty("user.dir")+ File.separator + "nowy.zip"));
//            else
//                wybieracz.setSelectedFile(new File(System.getProperty("user.dir")+ File.separator + wybieracz.getSelectedFile().getName() + ".zip"));
            int tmp = wybieracz.showDialog(rootPane, "kompresuj");
            
            if (tmp == JFileChooser.APPROVE_OPTION) {
                byte tmpData[] = new byte[BUFFER];        
                try {
                    ZipOutputStream zOutS = new ZipOutputStream (new BufferedOutputStream(new FileOutputStream(wybieracz.getSelectedFile()), BUFFER));

                    for (int i =0; i < modelListy.getSize(); i++) {
                        if ( !((File)modelListy.get(i)).isDirectory() )
                            zipuj (zOutS, (File)modelListy.get(i), tmpData, ((File)modelListy.get(i)).getPath());
                        else {
                            wypiszSciezki((File)modelListy.get(i));
                            for (int j=0; j < listaSciezek.size(); j++)
                                zipuj(zOutS, (File)listaSciezek.get(j), tmpData, ((File)modelListy.get(i)).getPath());
                            
                            listaSciezek.removeAll(listaSciezek);//zerujemy zbey nie bylo duplicate entry
                        } 
                    }
                    zOutS.close();
                }
                catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        private void zipuj (ZipOutputStream zOutS, File sciezkaPliku, byte[] tmpData, String sciezkaBazowa) throws IOException {
            
            BufferedInputStream inS = new BufferedInputStream(new FileInputStream(sciezkaPliku), BUFFER);    
                //tworzy plik w zip (z ta sama nazwa)
                //zOutS.putNextEntry(new ZipEntry(sciezkaPliku.getName()));
                //tworzy plik w zip ze sciezka ucieta od ostatniego file separatora(tworzy foldery)
                zOutS.putNextEntry(new ZipEntry(sciezkaPliku.getPath().substring(sciezkaBazowa.lastIndexOf(File.separator)+1)));
                System.out.println(sciezkaPliku);
                System.out.println(sciezkaBazowa);
                System.out.println(sciezkaBazowa.lastIndexOf(File.separator)+1);

                int counter;
                while ((counter = inS.read(tmpData, 0, BUFFER)) !=-1) //bo read zwraca -1 jak niema danych bo dojdzie do konca streamu
                    zOutS.write(tmpData, 0, counter);//zapisuje x bytes do output stream-u

                zOutS.closeEntry();
                inS.close();
        }
         private void wypiszSciezki(File sciezka) {
            String [] tabNazwPlikow = sciezka.list();
            for (int i = 0; i < tabNazwPlikow.length; i++)
            {
                File p = new File(sciezka.getPath(), tabNazwPlikow[i]);

                if (p.isFile())               //wypisz tylko pliki
                    listaSciezek.add(p);

                if (p.isDirectory())            //tylko foldery
                {
                    wypiszSciezki(new File(p.getPath()));
                }
                    
            }
        }
         
        public static final int BUFFER = 1024;
        ArrayList listaSciezek = new ArrayList();
    }
            
      
}
