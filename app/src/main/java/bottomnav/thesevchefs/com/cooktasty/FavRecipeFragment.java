package bottomnav.thesevchefs.com.cooktasty;

// FOR FAVOURITE ACTIVITY... CURRENTLY BLANK

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.android.volley.VolleyError;

import java.util.List;

import bottomnav.thesevchefs.com.cooktasty.cooktastyapi.APICallback;
import bottomnav.thesevchefs.com.cooktasty.cooktastyapi.RecipeAPI;
import bottomnav.thesevchefs.com.cooktasty.entity.Recipe;
import bottomnav.thesevchefs.com.cooktasty.utilities.EndlessRecyclerViewScrollListener;
import bottomnav.thesevchefs.com.cooktasty.utilities.RecipeListAdapter;

public class FavRecipeFragment extends Fragment implements RecipeListAdapter.ListItemClickListener {

    private static final int NUM_LIST_ITEMS = 100;

    private Context appContext;
    private String authToken;

    private RecipeListAdapter mRecipeListAdapter;

    @BindView(R.id.rv_favrecipelist) RecyclerView mRecyclerView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    private Unbinder unbinder;

    public static FavRecipeFragment newInstance() {
        FavRecipeFragment fragment = new FavRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        appContext = getActivity().getApplicationContext();
        authToken = MyApplication.getAuthToken();

        mRecipeListAdapter = new RecipeListAdapter(appContext, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadFavouriteRecipeListToRecyclerView(appContext, authToken, page, mRecipeListAdapter);
            }
        };

        loadFavouriteRecipeListToRecyclerView(appContext, authToken, 1, mRecipeListAdapter);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mRecipeListAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.addOnScrollListener(scrollListener);

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        showRecipeListDataView();
    }

    public void loadFavouriteRecipeListToRecyclerView(Context ctxt, String token, int getPage, final RecipeListAdapter mRecipeListAdapter){
        RecipeAPI.getFavouriteRecipeListAPI(ctxt, token, getPage, new APICallback(){
            public void onSuccess(Object result) {
                List<Recipe> recipelist = (List<Recipe>) result;
                mRecipeListAdapter.addRecipeListData(recipelist);
            }
            public void onError(Object error) {
                VolleyError volleyError = (VolleyError) error;
                volleyError.printStackTrace();
            }
        });
    }

    private void showRecipeListDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(Recipe recipe) {
        Intent intentToStartRecipeDetailActivity = new Intent(getActivity(), RecipeDetailsActivity.class);
        intentToStartRecipeDetailActivity.putExtra("RecipeId", recipe.id);
        startActivity(intentToStartRecipeDetailActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
