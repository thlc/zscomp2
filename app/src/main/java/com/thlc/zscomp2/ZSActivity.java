package com.thlc.zscomp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

public class ZSActivity extends AppCompatActivity {

    private TextView evTextView;
    private SeekBar evSeekBar;
    private TextView zoneTextView;
    private TextView shutterSpeedView;
    private TextView isoTextView;
    private SeekBar zoneSeekBar;
    private SeekBar isoSeekBar;
    private NumberPicker avPicker;

    private String[] apertures;
    private String[] zones;
    private String[] isoValues;

    private float selectedAperture;
    private float selectedEV;
    private float selectedISO;
    private float zoneOffset;

    private String[] shutterSpeedsStr;
    private double[] shutterSpeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zs);

        this.evTextView = (TextView)findViewById(R.id.evViewer);
        this.evSeekBar = (SeekBar)findViewById(R.id.evSeekBar);
        this.zoneTextView = (TextView)findViewById(R.id.zoneViewer);
        this.zoneSeekBar = (SeekBar)findViewById(R.id.zoneSeekBar);
        this.isoSeekBar = (SeekBar)findViewById(R.id.isoSeekBar);
        this.avPicker = (NumberPicker)findViewById(R.id.avPicker);
        this.shutterSpeedView = (TextView)findViewById(R.id.shutterSpeed);
        this.isoTextView = (TextView)findViewById(R.id.isoTextView);

        this.shutterSpeedsStr = new String[]{ "1/2", "1/4", "1/8", "1/15", "1/30", "1/60", "1/125", "1/250", "1/500", "1/1000", "1/2000"};
        this.shutterSpeeds = new double[]{ 1./2, 1./4, 1./8, 1./15, 1./30, 1./60, 1./125, 1./250, 1./500, 1./1000, 1./2000 };

        this.apertures = new String[]{"1.4", "2.0", "2.8", "4", "5.6", "6.7", "8", "9.5", "11", "16", "22"};
        this.zones = new String[]{ "0", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

        this.isoValues = new String[]{"25", "50", "64", "80",  "100", "125", "160", "200", "250", "320", "400", "800", "1600", "3200"};

        avPicker.setMaxValue(apertures.length - 1);
        avPicker.setMinValue(0);
        avPicker.setWrapSelectorWheel(false);
        avPicker.setDisplayedValues(apertures);
        avPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // FIXME: put that in proper preferences
        this.selectedISO = 400.0f;
        this.selectedAperture = Float.valueOf(apertures[0]);
        this.selectedEV = 15.0f;

        setupListeners();
        updateShutterSpeed();
    }

    private String getClosestShutterSpeed(double speed) {
        double sub;
        double oldSub = 0;
        int idx = 0;

        do {
            sub = Math.abs(speed - this.shutterSpeeds[idx]);
            if (sub == 0 || (oldSub != 0 && oldSub < sub))
                break;
            oldSub = sub;
            idx++;
        } while (idx < this.shutterSpeeds.length);

        return shutterSpeedsStr[idx - 1];
    }

    private double log2(double a)
    {
        return Math.log(a) / Math.log(2.0);
    }

    private int getDivider(double d) {
        int i;

        for (i = 1; i < 100000; i *= 10) {
            if ((int)(d / i) == 0) { return i; }
        }

        return 1;
    }

    private void updateShutterSpeed() {
      //  time = (N * N) / Math.pow(2, EV + log2(ISO / 100));
        double speed;
        double zonedEV;


        zonedEV = (double)this.selectedEV + (double)this.zoneOffset;

        speed = (this.selectedAperture * this.selectedAperture) / Math.pow(2, zonedEV + log2(this.selectedISO / 100.0));

        if (speed < 1./2000) {
            this.shutterSpeedView.setText("OVER");
            return;
        }

        if (speed < .7) {
            this.shutterSpeedView.setText(this.getClosestShutterSpeed(speed));
        } else {
            String speedText = Math.round(speed) + "''";
            this.shutterSpeedView.setText(speedText);
        }
    }

    private void setupListeners() {
        isoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                isoTextView.setText(isoValues[i]);
                selectedISO = Float.valueOf(isoValues[i].toString());

                updateShutterSpeed();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        evSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                evTextView.setText(Integer.toString(i));
                selectedEV = new Float(i);

                updateShutterSpeed();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        zoneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                zoneTextView.setText(zones[i]);
                zoneOffset = 10 - (i + 5);  // zone V == mid-gray
                updateShutterSpeed();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        avPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selectedAperture = new Float(apertures[i1]);
                updateShutterSpeed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
 //       getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
