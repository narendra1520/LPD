package com.example.narendra.lpd;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewRequest extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText sender,receiver,reccon,source,desti;
    Button request;
    ImageButton bsrc,bdesti;
    Spinner main,sub;
    SharedPreferences sh,userdata;
    SharedPreferences.Editor shed;
    Double srclat,srclong,deslat,deslong;
    boolean err=false;

    public NewRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_new_request, container, false);
        intil(v);
        return v;
    }

    private void intil(View v) {
        sender=(EditText)v.findViewById(R.id.edsender);
        receiver=(EditText)v.findViewById(R.id.edreciever);
        reccon=(EditText)v.findViewById(R.id.edreccon);
        source= (EditText) v.findViewById(R.id.edsource);
        source.setKeyListener(null);
        desti= (EditText) v.findViewById(R.id.eddesti);
        desti.setKeyListener(null);
        bsrc= (ImageButton) v.findViewById(R.id.btnsrc);
        bsrc.setOnClickListener(this);
        bdesti= (ImageButton) v.findViewById(R.id.btndesti);
        bdesti.setOnClickListener(this);
        request=(Button)v.findViewById(R.id.btnnext);
        request.setOnClickListener(this);
        main=(Spinner)v.findViewById(R.id.maincate);
        sub=(Spinner)v.findViewById(R.id.subcate);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.spnmain));
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        main.setAdapter(adp);
        sh = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        reccon.setText(sh.getString("con",null));
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        ActionBar actionBar= activity.getSupportActionBar();
        actionBar.setTitle(R.string.Newreq);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnsrc){
            PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();;
            try {
                Intent intent=builder.build(getActivity());
                startActivityForResult(intent,1);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        else if(v.getId()==R.id.btndesti){
            PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();;
            try {
                Intent intent=builder.build(getActivity());
                startActivityForResult(intent,2);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        else if(v.getId()==R.id.btnnext){
            if (source.getText().toString().isEmpty()){
                settingErr(source,"Source Address Required");
            } else if(desti.getText().toString().isEmpty() ) {
                settingErr(desti,"Destination Address Required");
            }else if (!new validate().valiName(sender.getText().toString())) {
                settingErr(sender,"Valid Name Required");
            } else if (!new validate().valiName(receiver.getText().toString())) {
                settingErr(receiver,"Valid Name Required");
            } else if (!new validate().valiContact(reccon.getText().toString())) {
                settingErr(reccon,getString(R.string.error_number));
            }else if (srclat.equals(deslat) && srclong.equals(deslong)) {
                Toast.makeText(getActivity(),"Source & Destination Must be Different", Toast.LENGTH_LONG).show();
            }else if(err){
                Toast.makeText(getActivity(), "Please Solve Error First", Toast.LENGTH_SHORT).show();
            }else {
                sh = getActivity().getSharedPreferences("package", Context.MODE_PRIVATE);
                shed = sh.edit();
                shed.putString("snd", sender.getText().toString())
                        .putString("rcv", receiver.getText().toString())
                        .putLong("srclat", Double.doubleToRawLongBits(srclat)).putLong("srclong", Double.doubleToRawLongBits(srclong))
                        .putLong("deslat", Double.doubleToRawLongBits(deslat)).putLong("deslong", Double.doubleToRawLongBits(deslong))
                        .putString("maincat", main.getSelectedItem().toString()).putString("subcat", sub.getSelectedItem().toString())
                        .putString("reccon", reccon.getText().toString())
                        .apply();
                android.support.v4.app.FragmentManager mn = getActivity().getSupportFragmentManager();
                mn.beginTransaction().replace(R.id.include, new Map_addr()).addToBackStack(null).commit();
            }
        }
    }

    private void settingErr(EditText edt, String s) {
        edt.setError(s);
        edt.requestFocus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==getActivity().RESULT_OK){
            Place place=PlacePicker.getPlace(data,getActivity());
            srclat=place.getLatLng().latitude;
            srclong=place.getLatLng().longitude;
            getPostal(place,srclat,srclong,source);
        }
        if(requestCode==2 && resultCode==getActivity().RESULT_OK){
            Place place=PlacePicker.getPlace(data,getActivity());
            desti.setText(place.getAddress());
            deslat=place.getLatLng().latitude;
            deslong=place.getLatLng().longitude;
            getPostal(place,deslat,deslong,desti);
        }
    }

    private void getPostal(Place place, Double lat, Double lng, EditText txtset){
        try {
            List<Address> addressList=new Geocoder(getActivity(), Locale.getDefault()).getFromLocation(lat, lng,1);
            if(addressList.get(0).getLocality()!=null&&addressList.get(0).getLocality().equalsIgnoreCase("ahmedabad")){
                txtset.setText(place.getAddress());
                txtset.setError(null);
                err=false;
            }else {
                txtset.setError("Service Not Available On This Address");
                err=true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAdp(ArrayAdapter<CharSequence> adp) {
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sub.setAdapter(adp);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayAdapter<CharSequence> adp;
        switch (position){
            case 0:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Devices));
                setAdp(adp);break;
            case 1:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Fashion));
                setAdp(adp);break;
            case 2:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Beauty));
                setAdp(adp);break;
            case 3:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Papers));
                setAdp(adp);break;
            case 4:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Toys));
                setAdp(adp);break;
            case 5:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Gift));
                setAdp(adp);break;
            case 6:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Kitchen));
                setAdp(adp);break;
            case 7:
                adp=new ArrayAdapter<CharSequence>(getContext(),android.R.layout.simple_spinner_dropdown_item,getResources().getTextArray(R.array.Food));
                setAdp(adp);break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        main.setPrompt("Required");
    }
}
