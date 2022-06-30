
package com.example.vida;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity   implements View.OnTouchListener {

    private Tablero fondo;
    private Casilla[][] casillas;
    private boolean  copia[][];
    private boolean activo = true;
    private CountDownTimer countDownTimer;
    private LinearLayout layout;

    private int fila=10,colu=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button res = (Button)findViewById(R.id.rest);
        Button play = (Button)findViewById(R.id.play);
        Button paus = (Button)findViewById(R.id.paus);

        paus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finGen();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inicioGen();
            }
        });

        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fondo.invalidate();
                for (int f = 0; f < fila; f++) {
                    for (int c = 0; c < colu; c++) {
                        casillas[f][c].destapado = false;
                    }
                }
            }
        });

        prueba(fila,colu);

    }

    public void prueba(int x, int y){
        layout = findViewById(R.id.layout1);
        fondo = new Tablero(this);
        fondo.setOnTouchListener(this);
        layout.addView(fondo);
        casillas = new Casilla[x][y];
        copia = new boolean[x][y];
        for (int f = 0; f < x; f++) {
            for (int c = 0; c < y; c++) {
                casillas[f][c] = new Casilla();
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        fondo.invalidate();
            for (int f = 0; f < fila; f++) {
                for (int c = 0; c < colu; c++) {
                    if (casillas[f][c].dentro((int) event.getX(), (int) event.getY())) {
                        if (!casillas[f][c].destapado) {
                            casillas[f][c].destapado = true;
                        } else {
                            casillas[f][c].destapado = false;
                        }
                    }
                }
            }
        return true;
    }

    public void copiar(){
        for(int i = 0 ; i < fila ; i++){
            for(int j = 0 ; j <colu ; j++){
                if(casillas[i][j].destapado){
                    copia[i][j] = casillas[i][j].destapado;
                }else{
                    copia[i][j] = false;
                }
            }
        }
    }

    public int contarvidas(int x, int y){
        int vida = 0;
        for(int i = -1 ; i < 2 ; i++){
            for(int j= -1 ; j < 2 ; j++){
                if(i==0 && j==0){

                }else {
                    try {
                        int xv = (x + i + fila) % fila;
                        int yv = (y + j + colu) % colu;
                        if (this.copia[xv][yv]) {
                            vida++;
                        }
                    } catch (Exception e) {
                    }
                    if (vida > 3) {
                        return vida;
                    }
                }
            }
        }
        return vida;
    }

    public void inicioGen() {
        play();
        // Infinite loop (restart)
        countDownTimer = new CountDownTimer(60000, 300) {
            @Override
            public void onTick(long millisUntilFinished) {
                play();
            }

            @Override
            public void onFinish() {
                // Infinite loop (restart)
                inicioGen();
            }
        };

        countDownTimer.start();
    }

    public void finGen() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void play(){
        try{
            copiar();
                  for(int i = 0 ; i < fila ; i++){
                      for(int j = 0 ; j < colu ; j++){
                          int vidas = contarvidas(i,j);
                          if(copia[i][j]){
                              if(vidas < 2 || vidas >3){
                                  casillas[i][j].destapado = false;
                              }
                          }else{
                              if(vidas==3){
                                  casillas[i][j].destapado = true;
                              }
                          }
                          fondo.invalidate();
                      }
                  }
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    class Tablero extends View {

        public Tablero(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            canvas.drawRGB(0, 0, 0);
            int ancho = 0;
            if (canvas.getWidth() < canvas.getHeight())
                ancho = fondo.getWidth();
            else
                ancho = fondo.getHeight();
            int anchocua = ancho/fila;
            Paint paint = new Paint();
            paint.setTextSize(50);
            Paint paint2 = new Paint();
            paint2.setTextSize(50);
            paint2.setTypeface(Typeface.DEFAULT_BOLD);
            paint2.setARGB(255, 0, 0, 255);
            Paint paintlinea1 = new Paint();
            paintlinea1.setARGB(255, 255, 255, 255);
            int filaact = 0;
            for (int f = 0; f < fila; f++) {
                for (int c = 0; c < colu; c++) {
                    casillas[f][c].fijarxy(c * anchocua, filaact, anchocua);
                    if (casillas[f][c].destapado == false) {
                        paint.setARGB(255, 185, 41, 10);
                    }else
                        paint.setARGB(255, 0, 0, 0);
                        canvas.drawRect(c * anchocua, filaact, c * anchocua
                                + anchocua - 2, filaact + anchocua - 2, paint);


                    // linea blanca
                    canvas.drawLine(c * anchocua, filaact, c * anchocua
                            + anchocua, filaact, paintlinea1);
                    canvas.drawLine(c * anchocua + anchocua - 1, filaact, c
                                    * anchocua + anchocua - 1, filaact + anchocua,
                            paintlinea1);

                    if (casillas[f][c].contenido >= 1
                            && casillas[f][c].contenido <= fila
                            && casillas[f][c].destapado)
                        canvas.drawText(
                                String.valueOf(casillas[f][c].contenido), c
                                        * anchocua + (anchocua / 2) - colu,
                                filaact + anchocua / 2, paint2);

                }
                filaact = filaact + anchocua;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.diez:
                layout.removeAllViews();
                this.fila=10;
                this.colu=10;
                prueba(fila,colu);
                fondo.invalidate();
                break;
            case R.id.veint:
                layout.removeAllViews();
                this.fila=20;
                this.colu=20;
                prueba(fila,colu);
                fondo.invalidate();
                break;
            case R.id.trein:
                layout.removeAllViews();
                this.fila=30;
                this.colu=30;
                prueba(fila,colu);
                fondo.invalidate();
                break;
            case R.id.cuaren:
                layout.removeAllViews();
                this.fila=40;
                this.colu=40;
                prueba(fila,colu);
                fondo.invalidate();
                break;
            case R.id.cincuent:
                layout.removeAllViews();
                this.fila=50;
                this.colu=50;
                prueba(fila,colu);
                fondo.invalidate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}